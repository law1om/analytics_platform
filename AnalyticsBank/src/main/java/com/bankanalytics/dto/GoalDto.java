package com.bankanalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private LocalDate deadline;
    private Integer progress;
    private Long divisionId;
    private String divisionName;
}
