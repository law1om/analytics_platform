package com.bankanalytics.service;

import com.bankanalytics.entity.Goal;
import com.bankanalytics.repository.GoalRepository;
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
public class GoalService {
    
    private final GoalRepository goalRepository;
    
    public List<Goal> getAllGoals() {
        log.debug("Fetching all goals");
        return goalRepository.findAll();
    }
    
    public Optional<Goal> getGoalById(Long id) {
        log.debug("Fetching goal by id: {}", id);
        return goalRepository.findById(id);
    }
    
    public List<Goal> getGoalsByDivision(Long divisionId) {
        log.debug("Fetching goals for division: {}", divisionId);
        return goalRepository.findByDivisionId(divisionId);
    }
    
    public List<Goal> getOverdueGoals() {
        log.debug("Fetching overdue goals");
        return goalRepository.findOverdueGoals();
    }
    
    public List<Goal> getCompletedGoals() {
        log.debug("Fetching completed goals");
        return goalRepository.findCompletedGoals();
    }
    
    public List<Goal> getGoalsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching goals between {} and {}", startDate, endDate);
        return goalRepository.findByDeadlineBetween(startDate, endDate);
    }
    
    public Goal createGoal(Goal goal) {
        log.info("Creating new goal: {}", goal.getTitle());
        
        if (goal.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Goal deadline cannot be in the past");
        }
        
        return goalRepository.save(goal);
    }
    
    public Goal updateGoal(Long id, Goal goalDetails) {
        log.info("Updating goal with id: {}", id);
        
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
        
        goal.setTitle(goalDetails.getTitle());
        goal.setDescription(goalDetails.getDescription());
        goal.setTargetValue(goalDetails.getTargetValue());
        goal.setCurrentValue(goalDetails.getCurrentValue());
        goal.setDeadline(goalDetails.getDeadline());
        goal.setDivision(goalDetails.getDivision());
        
        return goalRepository.save(goal);
    }
    
    public void deleteGoal(Long id) {
        log.info("Deleting goal with id: {}", id);
        
        if (!goalRepository.existsById(id)) {
            throw new RuntimeException("Goal not found with id: " + id);
        }
        
        goalRepository.deleteById(id);
    }
    
    public List<Goal> searchGoals(String keyword) {
        log.debug("Searching goals by keyword: {}", keyword);
        return goalRepository.findByKeyword(keyword);
    }
    
    public long getGoalCountByDivision(Long divisionId) {
        log.debug("Getting goal count for division: {}", divisionId);
        return goalRepository.countByDivisionId(divisionId);
    }
    
    public List<Goal> getGoalsByDivisionAndDateRange(Long divisionId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching goals for division {} between {} and {}", divisionId, startDate, endDate);
        return goalRepository.findByDivisionIdAndDeadlineBetween(divisionId, startDate, endDate);
    }
}
