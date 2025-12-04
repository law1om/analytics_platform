package com.bankanalytics.service;

import com.bankanalytics.entity.Goal;
import com.bankanalytics.entity.Task;
import com.bankanalytics.repository.GoalRepository;
import com.bankanalytics.repository.TaskRepository;
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
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    
    public List<Task> getAllTasks() {
        log.debug("Fetching all tasks");
        return taskRepository.findAll();
    }
    
    public Optional<Task> getTaskById(Long id) {
        log.debug("Fetching task by id: {}", id);
        return taskRepository.findById(id);
    }
    
    public List<Task> getTasksByGoal(Long goalId) {
        log.debug("Fetching tasks for goal: {}", goalId);
        return taskRepository.findByGoalId(goalId);
    }
    
    public List<Task> getTasksByUser(Long userId) {
        log.debug("Fetching tasks for user: {}", userId);
        return taskRepository.findByUserId(userId);
    }
    
    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        log.debug("Fetching tasks by status: {}", status);
        return taskRepository.findByStatus(status);
    }
    
    public List<Task> getOverdueTasks() {
        log.debug("Fetching overdue tasks");
        return taskRepository.findOverdueTasks();
    }
    
    public List<Task> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching tasks between {} and {}", startDate, endDate);
        return taskRepository.findByEndDateBetween(startDate, endDate);
    }
    
    public Task createTask(Task task) {
        log.info("Creating new task: {}", task.getTitle());
        
        if (task.getStartDate() != null && task.getEndDate() != null && 
            task.getStartDate().isAfter(task.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        if (task.getStatus() == null) {
            task.setStatus(Task.TaskStatus.NOT_STARTED);
        }
        
        Task savedTask = taskRepository.save(task);
        
        // Обновляем прогресс цели
        if (savedTask.getGoal() != null) {
            updateGoalProgress(savedTask.getGoal().getId());
        }
        
        return savedTask;
    }

    public Task updateTask(Long id, Task taskDetails) {
        log.info("Updating task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setStartDate(taskDetails.getStartDate());
        task.setEndDate(taskDetails.getEndDate());
        task.setProgress(taskDetails.getProgress());
        task.setExpectedResult(taskDetails.getExpectedResult());
        task.setActualResult(taskDetails.getActualResult());
        task.setImpact(taskDetails.getImpact());
        task.setGoal(taskDetails.getGoal());
        task.setUser(taskDetails.getUser());

        Task savedTask = taskRepository.save(task);
        
        // Обновляем прогресс цели
        if (savedTask.getGoal() != null) {
            updateGoalProgress(savedTask.getGoal().getId());
        }
        
        return savedTask;
    }
    
    private void updateGoalProgress(Long goalId) {
        List<Task> goalTasks = taskRepository.findByGoalId(goalId);
        
        if (goalTasks.isEmpty()) {
            return;
        }
        
        // Вычисляем средний прогресс всех задач цели
        int totalProgress = goalTasks.stream()
                .mapToInt(task -> task.getProgress() != null ? task.getProgress() : 0)
                .sum();
        
        int averageProgress = totalProgress / goalTasks.size();
        
        // Обновляем прогресс цели
        goalRepository.findById(goalId).ifPresent(goal -> {
            goal.setProgress(averageProgress);
            goalRepository.save(goal);
            log.info("Updated goal {} progress to {}%", goalId, averageProgress);
        });
    }
    
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);
        
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        
        taskRepository.deleteById(id);
    }
    
    public List<Task> searchTasks(String keyword) {
        log.debug("Searching tasks by keyword: {}", keyword);
        return taskRepository.findByKeyword(keyword);
    }
    
    public long getTaskCountByGoal(Long goalId) {
        log.debug("Getting task count for goal: {}", goalId);
        return taskRepository.countByGoalId(goalId);
    }
    
    public long getTaskCountByUser(Long userId) {
        log.debug("Getting task count for user: {}", userId);
        return taskRepository.countByUserId(userId);
    }
    
    public List<Task> getTasksByGoalAndStatus(Long goalId, Task.TaskStatus status) {
        log.debug("Fetching tasks for goal {} with status {}", goalId, status);
        return taskRepository.findByGoalIdAndStatus(goalId, status);
    }
    
    public List<Task> getTasksByUserAndStatus(Long userId, Task.TaskStatus status) {
        log.debug("Fetching tasks for user {} with status {}", userId, status);
        return taskRepository.findByUserIdAndStatus(userId, status);
    }
}
