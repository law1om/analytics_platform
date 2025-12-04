package com.bankanalytics.dto;

import com.bankanalytics.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private String expectedResult;
    private String actualResult;
    private Integer progress;
    private String impact;
    private Task.TaskStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long goalId;
    private String goalTitle;
    private Long userId;
    private String userName;
}
