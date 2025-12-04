package com.bankanalytics.service;

import com.bankanalytics.entity.Report;
import com.bankanalytics.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportService {
    
    private final ReportRepository reportRepository;
    
    public List<Report> getAllReports() {
        log.debug("Fetching all reports");
        return reportRepository.findAll();
    }
    
    public Optional<Report> getReportById(Long id) {
        log.debug("Fetching report by id: {}", id);
        return reportRepository.findById(id);
    }
    
    public List<Report> getReportsByUser(Long userId) {
        log.debug("Fetching reports for user: {}", userId);
        return reportRepository.findByUserId(userId);
    }
    
    public List<Report> getReportsByDivision(Long divisionId) {
        log.debug("Fetching reports for division: {}", divisionId);
        return reportRepository.findByDivisionId(divisionId);
    }
    
    public List<Report> getReportsByDate(LocalDate date) {
        log.debug("Fetching reports for date: {}", date);
        return reportRepository.findByReportDate(date);
    }
    
    public List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching reports between {} and {}", startDate, endDate);
        return reportRepository.findByReportDateBetween(startDate, endDate);
    }
    
    public List<Report> getReportsByUserAndDivision(Long userId, Long divisionId) {
        log.debug("Fetching reports for user {} and division {}", userId, divisionId);
        return reportRepository.findByUserIdAndDivisionId(userId, divisionId);
    }
    
    public List<Report> getReportsByDivisionAndDateRange(Long divisionId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching reports for division {} between {} and {}", divisionId, startDate, endDate);
        return reportRepository.findByDivisionIdAndReportDateBetween(divisionId, startDate, endDate);
    }
    
    public List<Report> searchReports(String keyword) {
        log.debug("Searching reports by keyword: {}", keyword);
        return reportRepository.findByKeyword(keyword);
    }
    
    public List<Report> getLatestReports() {
        log.debug("Fetching latest reports");
        return reportRepository.findAllOrderByReportDateDesc();
    }
    
    public Report createReport(Report report) {
        log.info("Creating new report: {}", report.getTitle());
        
        if (report.getReportDate() == null) {
            report.setReportDate(LocalDate.now());
        }
        
        return reportRepository.save(report);
    }
    
    public Report updateReport(Long id, Report reportDetails) {
        log.info("Updating report with id: {}", id);
        
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        
        report.setTitle(reportDetails.getTitle());
        report.setReportDate(reportDetails.getReportDate());
        report.setContent(reportDetails.getContent());
        report.setUser(reportDetails.getUser());
        report.setDivision(reportDetails.getDivision());
        
        return reportRepository.save(report);
    }
    
    public void deleteReport(Long id) {
        log.info("Deleting report with id: {}", id);
        
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("Report not found with id: " + id);
        }
        
        reportRepository.deleteById(id);
    }
    
    public long getReportCountByUser(Long userId) {
        log.debug("Getting report count for user: {}", userId);
        return reportRepository.countByUserId(userId);
    }
    
    public long getReportCountByDivision(Long divisionId) {
        log.debug("Getting report count for division: {}", divisionId);
        return reportRepository.countByDivisionId(divisionId);
    }
}
