package com.bankanalytics.repository;

import com.bankanalytics.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    
    List<Goal> findByDivisionId(Long divisionId);
    
    List<Goal> findByDeadlineBefore(LocalDate date);
    
    List<Goal> findByDeadlineBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT g FROM Goal g WHERE g.division.id = :divisionId AND g.deadline BETWEEN :startDate AND :endDate")
    List<Goal> findByDivisionIdAndDeadlineBetween(@Param("divisionId") Long divisionId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.division.id = :divisionId")
    long countByDivisionId(@Param("divisionId") Long divisionId);
    
    @Query("SELECT g FROM Goal g WHERE g.title LIKE %:keyword% OR g.description LIKE %:keyword%")
    List<Goal> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT g FROM Goal g WHERE g.currentValue >= g.targetValue")
    List<Goal> findCompletedGoals();
    
    @Query("SELECT g FROM Goal g WHERE g.deadline < CURRENT_DATE AND g.currentValue < g.targetValue")
    List<Goal> findOverdueGoals();
}
