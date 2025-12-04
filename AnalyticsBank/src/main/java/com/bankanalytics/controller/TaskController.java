package com.bankanalytics.controller;

import com.bankanalytics.entity.Task;
import com.bankanalytics.dto.TaskDto;
import com.bankanalytics.entity.TaskImportResult;
import com.bankanalytics.service.TaskImportService;
import com.bankanalytics.service.TaskService;
import com.bankanalytics.mapper.DtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    private final TaskImportService taskImportService;
    private final DtoMapper dtoMapper;
    
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        log.info("GET /tasks - Fetching all tasks");
        List<Task> tasks = taskService.getAllTasks();
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        log.info("GET /tasks/{} - Fetching task by id", id);
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(dtoMapper.toDto(task)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<TaskDto>> getTasksByGoal(@PathVariable Long goalId) {
        log.info("GET /tasks/goal/{} - Fetching tasks by goal", goalId);
        List<Task> tasks = taskService.getTasksByGoal(goalId);
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDto>> getTasksByUser(@PathVariable Long userId) {
        log.info("GET /tasks/user/{} - Fetching tasks by user", userId);
        List<Task> tasks = taskService.getTasksByUser(userId);
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        log.info("GET /tasks/status/{} - Fetching tasks by status", status);
        List<Task> tasks = taskService.getTasksByStatus(status);
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        log.info("GET /tasks/overdue - Fetching overdue tasks");
        List<Task> tasks = taskService.getOverdueTasks();
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<TaskDto>> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /tasks/date-range - Fetching tasks between {} and {}", startDate, endDate);
        List<Task> tasks = taskService.getTasksByDateRange(startDate, endDate);
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/goal/{goalId}/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByGoalAndStatus(@PathVariable Long goalId, 
                                                            @PathVariable Task.TaskStatus status) {
        log.info("GET /tasks/goal/{}/status/{} - Fetching tasks by goal and status", goalId, status);
        List<Task> tasks = taskService.getTasksByGoalAndStatus(goalId, status);
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByUserAndStatus(@PathVariable Long userId, 
                                                            @PathVariable Task.TaskStatus status) {
        log.info("GET /tasks/user/{}/status/{} - Fetching tasks by user and status", userId, status);
        List<Task> tasks = taskService.getTasksByUserAndStatus(userId, status);
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTasks(@RequestParam String keyword) {
        log.info("GET /tasks/search?keyword={} - Searching tasks", keyword);
        List<Task> tasks = taskService.searchTasks(keyword);
        List<TaskDto> result = tasks.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/goal/{goalId}/count")
    public ResponseEntity<Long> getTaskCountByGoal(@PathVariable Long goalId) {
        log.info("GET /tasks/goal/{}/count - Getting task count", goalId);
        long count = taskService.getTaskCountByGoal(goalId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getTaskCountByUser(@PathVariable Long userId) {
        log.info("GET /tasks/user/{}/count - Getting task count", userId);
        long count = taskService.getTaskCountByUser(userId);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody Task task) {
        log.info("POST /tasks - Creating new task: {}", task.getTitle());
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDto(createdTask));
        } catch (IllegalArgumentException e) {
            log.error("Error creating task: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/import")
    public ResponseEntity<TaskImportResult> importTasks(@RequestParam("file") MultipartFile file) {
        log.info("POST /tasks/import - Importing tasks from Excel: {}", file.getOriginalFilename());
        TaskImportResult result = taskImportService.importFromExcel(file);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, 
                                         @Valid @RequestBody Task taskDetails) {
        log.info("PUT /tasks/{} - Updating task", id);
        try {
            Task updatedTask = taskService.updateTask(id, taskDetails);
            return ResponseEntity.ok(dtoMapper.toDto(updatedTask));
        } catch (RuntimeException e) {
            log.error("Error updating task: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /tasks/{} - Deleting task", id);
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting task: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}

