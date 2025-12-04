package com.bankanalytics.controller;

import com.bankanalytics.entity.Report;
import com.bankanalytics.dto.ReportDto;
import com.bankanalytics.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReportController {
    
    private final ReportService reportService;
    
    @GetMapping
    public ResponseEntity<List<ReportDto>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports.stream().map(this::toDto).collect(Collectors.toList()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .map(report -> ResponseEntity.ok(toDto(report)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/division/{divisionId}")
    public ResponseEntity<List<ReportDto>> getReportsByDivision(@PathVariable Long divisionId) {
        List<Report> reports = reportService.getReportsByDivision(divisionId);
        return ResponseEntity.ok(reports.stream().map(this::toDto).collect(Collectors.toList()));
    }
    
    @PostMapping
    public ResponseEntity<ReportDto> createReport(@Valid @RequestBody Report report) {
        Report createdReport = reportService.createReport(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(createdReport));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ReportDto> updateReport(@PathVariable Long id, @Valid @RequestBody Report reportDetails) {
        try {
            Report updatedReport = reportService.updateReport(id, reportDetails);
            return ResponseEntity.ok(toDto(updatedReport));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ReportDto toDto(Report report) {
        Long userId = report.getUser() != null ? report.getUser().getId() : null;
        String userName = report.getUser() != null ? report.getUser().getName() : null;
        Long divisionId = report.getDivision() != null ? report.getDivision().getId() : null;
        String divisionName = report.getDivision() != null ? report.getDivision().getName() : null;
        return new ReportDto(
            report.getId(),
            report.getTitle(),
            report.getReportDate(),
            report.getContent(),
            userId,
            userName,
            divisionId,
            divisionName
        );
    }
}

