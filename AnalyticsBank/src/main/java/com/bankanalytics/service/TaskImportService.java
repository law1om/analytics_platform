package com.bankanalytics.service;

import com.bankanalytics.entity.Division;
import com.bankanalytics.entity.Task;
import com.bankanalytics.entity.TaskImportResult;
import com.bankanalytics.repository.DivisionRepository;
import com.bankanalytics.entity.Goal;
import com.bankanalytics.repository.GoalRepository;
import com.bankanalytics.entity.User;
import com.bankanalytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskImportService {

    private final DivisionRepository divisionRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;

    public TaskImportResult importFromExcel(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int total = 0;
        int success = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            int headerRowIndex = findHeaderRow(sheet);
            if (headerRowIndex == -1) {
                errors.add("Не найдена строка с заголовками (первая колонка должна содержать '№')");
                return new TaskImportResult(0, 0, 1, errors);
            }
            
            log.info("Найдена строка заголовков на позиции: {}", headerRowIndex + 1);
            
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                int rowNum = i + 1;
                
                String rowNumber = getStringCell(row, 0);
                if (rowNumber == null || rowNumber.trim().isEmpty()) {
                    log.debug("Пропускаем строку {} - нет номера", rowNum);
                    continue;
                }
                
                total++;
                try {
                    String divisionName = getStringCell(row, 1);    // B: Наименование блока (подразделение)
                    String initiativeName = getStringCell(row, 2);  // C: ИНИЦИАТИВА (цель)
                    String taskTitle = getStringCell(row, 3);       // D: ЗАДАЧИ И МЕРОПРИЯТИЯ
                    String taskDescription = getStringCell(row, 4); // E: ОПИСАНИЕ ЗАДАЧИ
                    String expectedResult = getStringCell(row, 5);  // F: Показатель результата
                    String blockName = getStringCell(row, 6);       // G: ОТВЕТСТВЕННЫЙ ИСПОЛНИТЕЛЬ (блок)
                    String targetValue = getStringCell(row, 7);     // H: Целевое значение
                    String statusStr = getStringCell(row, 8);       // I: Статус
                    String progressStr = getStringCell(row, 9);     // J: Прогресс
                    LocalDate startDate = getDateCell(row, 10);     // K: Плановая дата начала
                    LocalDate endDate = getDateCell(row, 11);       // L: Плановая дата окончания
                    String actualResult = getStringCell(row, 12);   // M: Фактический результат
                    String impact = getStringCell(row, 13);         // N: Оценочный эффект

                    if (divisionName == null || divisionName.isBlank()) {
                        log.warn("Строка {} (№{}): Пустое наименование блока, пропускаем", rowNum, rowNumber);
                        continue;
                    }
                    
                    if (initiativeName == null || initiativeName.isBlank()) {
                        throw new IllegalArgumentException("Пустое наименование инициативы");
                    }

                    Division division = resolveDivision(divisionName, blockName)
                            .orElseThrow(() -> new IllegalArgumentException("Не удалось создать/найти подразделение: " + divisionName));

                    LocalDate goalDeadline = endDate != null ? endDate : LocalDate.now().plusYears(1);
                    Goal goal = resolveOrCreateGoal(division, initiativeName, null, targetValue, goalDeadline)
                            .orElseThrow(() -> new IllegalArgumentException("Не удалось создать/найти инициативу: " + initiativeName));

                    User user = resolveUserByBlock(blockName, division)
                            .orElse(null);

                    Task task = new Task();
                    task.setTitle(taskTitle);
                    task.setDescription(taskDescription);
                    task.setExpectedResult(expectedResult);
                    task.setActualResult(actualResult);
                    task.setImpact(impact);
                    task.setGoal(goal);
                    task.setUser(user);
                    task.setStartDate(startDate);
                    task.setEndDate(endDate);

                    if (progressStr != null && !progressStr.isBlank()) {
                        try {
                            task.setProgress(Integer.parseInt(progressStr.trim()));
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Некорректный прогресс: " + progressStr);
                        }
                    }

                    Task.TaskStatus status = resolveStatus(statusStr);
                    task.setStatus(status);

                    taskService.createTask(task);
                    success++;
                } catch (Exception e) {
                    log.error("Ошибка обработки строки {} (№{}): {}", rowNum, rowNumber, e.getMessage());
                    errors.add("Строка " + rowNum + " (№" + rowNumber + "): " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Ошибка чтения Excel файла: {}", e.getMessage());
            errors.add("Ошибка чтения файла: " + e.getMessage());
        }

        int errorCount = errors.size();
        return new TaskImportResult(total, success, errorCount, errors);
    }

    private Optional<Division> resolveDivision(String divisionName, String blockName) {
        if (divisionName == null || divisionName.isBlank()) {
            return Optional.empty();
        }
        String trimmedDivision = divisionName.trim();
        String trimmedBlock = blockName != null ? blockName.trim() : null;

        List<Division> list = divisionRepository.findByNameContaining(trimmedDivision);
        Division division;
        
        if (!list.isEmpty()) {
            division = list.get(0);
        } else {
            division = new Division();
            division.setName(trimmedDivision);
            division.setBlocks(new ArrayList<>());
            log.info("Создано новое подразделение из импорта: {} (id={})", trimmedDivision, division.getId());
        }
        
        if (trimmedBlock != null && !trimmedBlock.isBlank()) {
            if (division.getBlocks() == null) {
                division.setBlocks(new ArrayList<>());
            }
            if (!division.getBlocks().contains(trimmedBlock)) {
                division.getBlocks().add(trimmedBlock);
                log.info("Добавлен блок '{}' в подразделение '{}'", trimmedBlock, trimmedDivision);
            }
        }
        
        Division saved = divisionRepository.save(division);
        return Optional.of(saved);
    }

    private Optional<Goal> resolveGoal(Division division, String initiativeName) {
        if (initiativeName == null || initiativeName.isBlank()) {
            return Optional.empty();
        }
        List<Goal> goals = goalRepository.findByDivisionId(division.getId());
        return goals.stream()
                .filter(g -> initiativeName.trim().equalsIgnoreCase(g.getTitle()))
                .findFirst();
    }

    private int findHeaderRow(Sheet sheet) {
        for (int i = 0; i <= Math.min(20, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            String firstCell = getStringCell(row, 0);
            if (firstCell != null && firstCell.trim().equals("№")) {
                return i;
            }
        }
        return -1;
    }

    private Optional<Goal> resolveOrCreateGoal(Division division, String initiativeName, String description, String targetValue, LocalDate deadline) {
        if (initiativeName == null || initiativeName.isBlank()) {
            return Optional.empty();
        }
        
        List<Goal> goals = goalRepository.findByDivisionId(division.getId());
        Optional<Goal> existingGoal = goals.stream()
                .filter(g -> initiativeName.trim().equalsIgnoreCase(g.getTitle()))
                .findFirst();
                
        if (existingGoal.isPresent()) {
            return existingGoal;
        }
        
        Goal newGoal = new Goal();
        newGoal.setTitle(initiativeName.trim());
        newGoal.setDescription(description);
        newGoal.setDivision(division);
        newGoal.setDeadline(deadline); // Устанавливаем обязательный deadline
        
        if (targetValue != null && !targetValue.isBlank()) {
            try {
                newGoal.setTargetValue(new BigDecimal(targetValue.trim()));
            } catch (NumberFormatException e) {
                log.warn("Не удалось распарсить целевое значение: {}", targetValue);
            }
        }
        
        Goal saved = goalRepository.save(newGoal);
        log.info("Создана новая инициатива: {} для подразделения {}", initiativeName, division.getName());
        return Optional.of(saved);
    }
    

    private Optional<User> resolveUserByBlock(String block, Division division) {
        if (block == null || block.isBlank()) {
            return Optional.empty();
        }
        
        List<User> users = userRepository.findByDivisionId(division.getId());
        Optional<User> userInBlock = users.stream()
                .filter(u -> block.trim().equalsIgnoreCase(u.getBlock()))
                .findFirst();
                
        if (userInBlock.isPresent()) {
            return userInBlock;
        }
        
        // Если не найден - возвращаем первого пользователя из подразделения
        if (!users.isEmpty()) {
            return Optional.of(users.get(0));
        }
        
        return Optional.empty();
    }

    private String getStringCell(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private LocalDate getDateCell(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String value = getStringCell(row, index);
        if (value == null || value.isBlank()) {
            return null;
        }

        String trimmed = value.trim();
        String firstToken = trimmed.split("\\s+")[0];
        try {
            if (firstToken.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                return LocalDate.parse(firstToken, formatter);
            }
            return LocalDate.parse(firstToken);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Task.TaskStatus resolveStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            return Task.TaskStatus.NOT_STARTED;
        }
        String normalized = statusStr.trim().toUpperCase();
        switch (normalized) {
            case "NOT_STARTED":
            case "НЕ НАЧАТА":
                return Task.TaskStatus.NOT_STARTED;
            case "IN_PROGRESS":
            case "В РАБОТЕ":
                return Task.TaskStatus.IN_PROGRESS;
            case "COMPLETED":
            case "ЗАВЕРШЕНА":
                return Task.TaskStatus.COMPLETED;
            case "ON_HOLD":
            case "ПРИОСТАНОВЛЕНА":
                return Task.TaskStatus.ON_HOLD;
            case "CANCELLED":
            case "ОТМЕНЕНА":
                return Task.TaskStatus.CANCELLED;
            default:
                return Task.TaskStatus.NOT_STARTED;
        }
    }
}
