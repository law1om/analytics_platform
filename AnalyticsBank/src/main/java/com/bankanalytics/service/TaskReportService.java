package com.bankanalytics.service;

import com.bankanalytics.entity.TaskReport;
import com.bankanalytics.repository.TaskReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskReportService {
    
    private final TaskReportRepository taskReportRepository;
    
    public List<TaskReport> getAllTaskReports() {
        log.debug("Fetching all task reports");
        return taskReportRepository.findAll();
    }
    
    public Optional<TaskReport> getTaskReportById(Long id) {
        log.debug("Fetching task report by id: {}", id);
        return taskReportRepository.findById(id);
    }
    
    public List<TaskReport> getTaskReportsByTask(Long taskId) {
        log.debug("Fetching task reports for task: {}", taskId);
        return taskReportRepository.findByTaskId(taskId);
    }
    
    public List<TaskReport> getTaskReportsByReport(Long reportId) {
        log.debug("Fetching task reports for report: {}", reportId);
        return taskReportRepository.findByReportId(reportId);
    }
    
    public List<TaskReport> getTaskReportsByTaskAndReport(Long taskId, Long reportId) {
        log.debug("Fetching task reports for task {} and report {}", taskId, reportId);
        return taskReportRepository.findByTaskIdAndReportId(taskId, reportId);
    }
    
    public List<TaskReport> getTaskReportsByMinProgress(BigDecimal minProgress) {
        log.debug("Fetching task reports with min progress: {}", minProgress);
        return taskReportRepository.findByProgressGreaterThanEqual(minProgress);
    }
    
    public List<TaskReport> searchTaskReports(String keyword) {
        log.debug("Searching task reports by keyword: {}", keyword);
        return taskReportRepository.findByNotesContaining(keyword);
    }
    
    public TaskReport createTaskReport(TaskReport taskReport) {
        log.info("Creating new task report for task: {}", taskReport.getTask().getId());
        
        if (taskReport.getProgress() != null && 
            (taskReport.getProgress().compareTo(BigDecimal.ZERO) < 0 || 
             taskReport.getProgress().compareTo(new BigDecimal("100")) > 0)) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }
        
        return taskReportRepository.save(taskReport);
    }
    
    public TaskReport updateTaskReport(Long id, TaskReport taskReportDetails) {
        log.info("Updating task report with id: {}", id);
        
        TaskReport taskReport = taskReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task report not found with id: " + id));
        
        taskReport.setTask(taskReportDetails.getTask());
        taskReport.setReport(taskReportDetails.getReport());
        taskReport.setProgress(taskReportDetails.getProgress());
        taskReport.setNotes(taskReportDetails.getNotes());
        
        return taskReportRepository.save(taskReport);
    }
    
    public void deleteTaskReport(Long id) {
        log.info("Deleting task report with id: {}", id);
        
        if (!taskReportRepository.existsById(id)) {
            throw new RuntimeException("Task report not found with id: " + id);
        }
        
        taskReportRepository.deleteById(id);
    }
    
    public long getTaskReportCountByTask(Long taskId) {
        log.debug("Getting task report count for task: {}", taskId);
        return taskReportRepository.countByTaskId(taskId);
    }
    
    public long getTaskReportCountByReport(Long reportId) {
        log.debug("Getting task report count for report: {}", reportId);
        return taskReportRepository.countByReportId(reportId);
    }
    
    public BigDecimal getAverageProgressByTask(Long taskId) {
        log.debug("Getting average progress for task: {}", taskId);
        BigDecimal averageProgress = taskReportRepository.getAverageProgressByTaskId(taskId);
        return averageProgress != null ? averageProgress : BigDecimal.ZERO;
    }
}
