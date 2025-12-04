package com.bankanalytics.service;

import com.bankanalytics.entity.Task;
import com.bankanalytics.entity.Goal;
import com.bankanalytics.repository.TaskRepository;
import com.bankanalytics.repository.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private Goal testGoal;

    @BeforeEach
    void setUp() {
        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setTitle("Test Goal");
        testGoal.setProgress(0);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(Task.TaskStatus.NOT_STARTED);
        testTask.setProgress(0);
        testTask.setStartDate(LocalDate.now());
        testTask.setEndDate(LocalDate.now().plusDays(30));
        testTask.setGoal(testGoal);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertEquals(1, result.size());
        assertEquals(testTask.getTitle(), result.get(0).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // Act
        Optional<Task> result = taskService.getTaskById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTask.getTitle(), result.get().getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void createTask_WithValidDates_ShouldCreateTask() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        Task result = taskService.createTask(testTask);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_WithInvalidDates_ShouldThrowException() {
        // Arrange
        testTask.setStartDate(LocalDate.now().plusDays(10));
        testTask.setEndDate(LocalDate.now());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(testTask);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_WithGoal_ShouldUpdateGoalProgress() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskRepository.findByGoalId(1L)).thenReturn(Arrays.asList(testTask));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        // Act
        Task result = taskService.createTask(testTask);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskRepository, times(1)).findByGoalId(1L);
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateTask() {
        // Arrange
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
        updatedTask.setProgress(50);
        updatedTask.setStartDate(testTask.getStartDate());
        updatedTask.setEndDate(testTask.getEndDate());
        updatedTask.setGoal(testGoal);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskRepository.findByGoalId(1L)).thenReturn(Arrays.asList(testTask));
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        // Act
        Task result = taskService.updateTask(1L, updatedTask);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskRepository, times(1)).findByGoalId(1L);
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldDeleteTask() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void getTasksByStatus_ShouldReturnTasksWithStatus() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByStatus(Task.TaskStatus.NOT_STARTED)).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getTasksByStatus(Task.TaskStatus.NOT_STARTED);

        // Assert
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByStatus(Task.TaskStatus.NOT_STARTED);
    }

    @Test
    void getOverdueTasks_ShouldReturnOverdueTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findOverdueTasks()).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getOverdueTasks();

        // Assert
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findOverdueTasks();
    }
}
