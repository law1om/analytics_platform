package com.bankanalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskReportDto {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long reportId;
    private String reportTitle;
    private BigDecimal progress;
    private String notes;
}
