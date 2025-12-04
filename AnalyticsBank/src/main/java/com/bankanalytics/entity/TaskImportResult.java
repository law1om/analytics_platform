package com.bankanalytics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskImportResult {
    private int totalRows;
    private int successCount;
    private int errorCount;
    private List<String> errors;
}
