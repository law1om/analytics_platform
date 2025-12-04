package com.bankanalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskExcelRowDto {

    //Наименование блока
    private String divisionName;

    //Инициатива (цель в системе)
    private String initiativeName;

    //Задача/мероприятие в рамках инициативы
    private String taskTitle;

    //Описание задачи/мероприятия
    private String taskDescription;

    //Ответственный исполнитель
    private String responsibleName;

    //Ожидаемый результат
    private String expectedResult;

    //Статус исполнения (текстовое значение из Excel)
    private String statusText;

    //Прогресс, %
    private Integer progress;

    //Дата начала
    private LocalDate startDate;

    //Дата завершения
    private LocalDate endDate;

    //Фактический результат
    private String actualResult;

    //Оценочный эффект на деятельность банка / финансовые показатели
    private String impact;
}
