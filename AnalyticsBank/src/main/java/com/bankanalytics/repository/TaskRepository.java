package com.bankanalytics.repository;

import com.bankanalytics.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    @Override
    List<Task> findAll();
    
    @Override
    Optional<Task> findById(Long id);
    
    List<Task> findByGoalId(Long goalId);
    
    List<Task> findByUserId(Long userId);
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    List<Task> findByEndDateBefore(LocalDate date);
    
    List<Task> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT t FROM Task t WHERE t.goal.id = :goalId AND t.status = :status")
    List<Task> findByGoalIdAndStatus(@Param("goalId") Long goalId, 
                                    @Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    List<Task> findByUserIdAndStatus(@Param("userId") Long userId, 
                                    @Param("status") Task.TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.goal.id = :goalId")
    long countByGoalId(@Param("goalId") Long goalId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Task> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT t FROM Task t WHERE t.endDate < CURRENT_DATE AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks();
}
