package com.bankanalytics.mapper;

import com.bankanalytics.dto.*;
import com.bankanalytics.entity.*;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping entities to DTOs
 * Eliminates code duplication across controllers
 */
@Component
public class DtoMapper {
    
    public UserDto toDto(User user) {
        if (user == null) return null;
        
        Long divisionId = user.getDivision() != null ? user.getDivision().getId() : null;
        String divisionName = user.getDivision() != null ? user.getDivision().getName() : null;
        
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            divisionId,
            divisionName,
            user.getBlock()
        );
    }
    
    public TaskDto toDto(Task task) {
        if (task == null) return null;
        
        Long goalId = task.getGoal() != null ? task.getGoal().getId() : null;
        String goalTitle = task.getGoal() != null ? task.getGoal().getTitle() : null;
        Long userId = task.getUser() != null ? task.getUser().getId() : null;
        String userName = task.getUser() != null ? task.getUser().getName() : null;
        
        return new TaskDto(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getExpectedResult(),
            task.getActualResult(),
            task.getProgress(),
            task.getImpact(),
            task.getStatus(),
            task.getStartDate(),
            task.getEndDate(),
            goalId,
            goalTitle,
            userId,
            userName
        );
    }
    
    public GoalDto toDto(Goal goal) {
        if (goal == null) return null;
        
        Long divisionId = goal.getDivision() != null ? goal.getDivision().getId() : null;
        String divisionName = goal.getDivision() != null ? goal.getDivision().getName() : null;
        
        return new GoalDto(
            goal.getId(),
            goal.getTitle(),
            goal.getDescription(),
            goal.getTargetValue(),
            goal.getCurrentValue(),
            goal.getDeadline(),
            goal.getProgress(),
            divisionId,
            divisionName
        );
    }
    
    public DivisionDto toDto(Division division) {
        if (division == null) return null;
        
        return new DivisionDto(
            division.getId(),
            division.getName(),
            division.getBlocks()
        );
    }
    
    public ReportDto toDto(Report report) {
        if (report == null) return null;
        
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
    
    public TaskReportDto toDto(TaskReport taskReport) {
        if (taskReport == null) return null;
        
        Long taskId = taskReport.getTask() != null ? taskReport.getTask().getId() : null;
        String taskTitle = taskReport.getTask() != null ? taskReport.getTask().getTitle() : null;
        Long reportId = taskReport.getReport() != null ? taskReport.getReport().getId() : null;
        String reportTitle = taskReport.getReport() != null ? taskReport.getReport().getTitle() : null;
        
        return new TaskReportDto(
            taskReport.getId(),
            taskId,
            taskTitle,
            reportId,
            reportTitle,
            taskReport.getProgress(),
            taskReport.getNotes()
        );
    }
}
