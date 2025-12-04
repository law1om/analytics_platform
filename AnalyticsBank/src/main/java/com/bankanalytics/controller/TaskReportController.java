package com.bankanalytics.controller;

import com.bankanalytics.entity.TaskReport;
import com.bankanalytics.service.TaskReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/task-reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TaskReportController {
    
    private final TaskReportService taskReportService;
    
    @GetMapping
    public ResponseEntity<List<TaskReport>> getAllTaskReports() {
        log.info("GET /task-reports - Fetching all task reports");
        List<TaskReport> taskReports = taskReportService.getAllTaskReports();
        return ResponseEntity.ok(taskReports);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskReport> getTaskReportById(@PathVariable Long id) {
        log.info("GET /task-reports/{} - Fetching task report by id", id);
        return taskReportService.getTaskReportById(id)
                .map(taskReport -> ResponseEntity.ok(taskReport))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskReport>> getTaskReportsByTask(@PathVariable Long taskId) {
        log.info("GET /task-reports/task/{} - Fetching task reports by task", taskId);
        List<TaskReport> taskReports = taskReportService.getTaskReportsByTask(taskId);
        return ResponseEntity.ok(taskReports);
    }
    
    @GetMapping("/report/{reportId}")
    public ResponseEntity<List<TaskReport>> getTaskReportsByReport(@PathVariable Long reportId) {
        log.info("GET /task-reports/report/{} - Fetching task reports by report", reportId);
        List<TaskReport> taskReports = taskReportService.getTaskReportsByReport(reportId);
        return ResponseEntity.ok(taskReports);
    }
    
    @GetMapping("/task/{taskId}/report/{reportId}")
    public ResponseEntity<List<TaskReport>> getTaskReportsByTaskAndReport(@PathVariable Long taskId, 
                                                                        @PathVariable Long reportId) {
        log.info("GET /task-reports/task/{}/report/{} - Fetching task reports by task and report", taskId, reportId);
        List<TaskReport> taskReports = taskReportService.getTaskReportsByTaskAndReport(taskId, reportId);
        return ResponseEntity.ok(taskReports);
    }
    
    @GetMapping("/progress/{minProgress}")
    public ResponseEntity<List<TaskReport>> getTaskReportsByMinProgress(@PathVariable BigDecimal minProgress) {
        log.info("GET /task-reports/progress/{} - Fetching task reports with min progress", minProgress);
        List<TaskReport> taskReports = taskReportService.getTaskReportsByMinProgress(minProgress);
        return ResponseEntity.ok(taskReports);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TaskReport>> searchTaskReports(@RequestParam String keyword) {
        log.info("GET /task-reports/search?keyword={} - Searching task reports", keyword);
        List<TaskReport> taskReports = taskReportService.searchTaskReports(keyword);
        return ResponseEntity.ok(taskReports);
    }
    
    @GetMapping("/task/{taskId}/count")
    public ResponseEntity<Long> getTaskReportCountByTask(@PathVariable Long taskId) {
        log.info("GET /task-reports/task/{}/count - Getting task report count", taskId);
        long count = taskReportService.getTaskReportCountByTask(taskId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/report/{reportId}/count")
    public ResponseEntity<Long> getTaskReportCountByReport(@PathVariable Long reportId) {
        log.info("GET /task-reports/report/{}/count - Getting task report count", reportId);
        long count = taskReportService.getTaskReportCountByReport(reportId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/task/{taskId}/average-progress")
    public ResponseEntity<BigDecimal> getAverageProgressByTask(@PathVariable Long taskId) {
        log.info("GET /task-reports/task/{}/average-progress - Getting average progress", taskId);
        BigDecimal averageProgress = taskReportService.getAverageProgressByTask(taskId);
        return ResponseEntity.ok(averageProgress);
    }
    
    @PostMapping
    public ResponseEntity<TaskReport> createTaskReport(@Valid @RequestBody TaskReport taskReport) {
        log.info("POST /task-reports - Creating new task report");
        TaskReport createdTaskReport = taskReportService.createTaskReport(taskReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskReport);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskReport> updateTaskReport(@PathVariable Long id, 
                                                     @Valid @RequestBody TaskReport taskReportDetails) {
        log.info("PUT /task-reports/{} - Updating task report", id);
        try {
            TaskReport updatedTaskReport = taskReportService.updateTaskReport(id, taskReportDetails);
            return ResponseEntity.ok(updatedTaskReport);
        } catch (RuntimeException e) {
            log.error("Error updating task report: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskReport(@PathVariable Long id) {
        log.info("DELETE /task-reports/{} - Deleting task report", id);
        try {
            taskReportService.deleteTaskReport(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting task report: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
