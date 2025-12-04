package com.bankanalytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long id;
    private String title;
    private LocalDate reportDate;
    private String content;
    private Long userId;
    private String userName;
    private Long divisionId;
    private String divisionName;
}
