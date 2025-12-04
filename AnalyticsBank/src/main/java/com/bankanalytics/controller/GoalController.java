package com.bankanalytics.controller;

import com.bankanalytics.entity.Goal;
import com.bankanalytics.dto.GoalDto;
import com.bankanalytics.service.GoalService;
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
@RequestMapping("/goals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GoalController {
    
    private final GoalService goalService;
    
    @GetMapping
    public ResponseEntity<List<GoalDto>> getAllGoals() {
        log.info("GET /goals - Fetching all goals");
        List<Goal> goals = goalService.getAllGoals();
        List<GoalDto> result = goals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GoalDto> getGoalById(@PathVariable Long id) {
        log.info("GET /goals/{} - Fetching goal by id", id);
        return goalService.getGoalById(id)
                .map(goal -> ResponseEntity.ok(toDto(goal)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/division/{divisionId}")
    public ResponseEntity<List<GoalDto>> getGoalsByDivision(@PathVariable Long divisionId) {
        log.info("GET /goals/division/{} - Fetching goals by division", divisionId);
        List<Goal> goals = goalService.getGoalsByDivision(divisionId);
        List<GoalDto> result = goals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/overdue")
    public ResponseEntity<List<GoalDto>> getOverdueGoals() {
        log.info("GET /goals/overdue - Fetching overdue goals");
        List<Goal> goals = goalService.getOverdueGoals();
        List<GoalDto> result = goals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/completed")
    public ResponseEntity<List<GoalDto>> getCompletedGoals() {
        log.info("GET /goals/completed - Fetching completed goals");
        List<Goal> goals = goalService.getCompletedGoals();
        List<GoalDto> result = goals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<List<GoalDto>> getGoalsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /goals/date-range - Fetching goals between {} and {}", startDate, endDate);
        List<Goal> goals = goalService.getGoalsByDateRange(startDate, endDate);
        List<GoalDto> result = goals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/division/{divisionId}/date-range")
    public ResponseEntity<List<GoalDto>> getGoalsByDivisionAndDateRange(
            @PathVariable Long divisionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /goals/division/{}/date-range - Fetching goals for division between {} and {}", 
                divisionId, startDate, endDate);
        List<Goal> goals = goalService.getGoalsByDivisionAndDateRange(divisionId, startDate, endDate);
        List<GoalDto> result = goals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<GoalDto>> searchGoals(@RequestParam String keyword) {
        log.info("GET /goals/search?keyword={} - Searching goals", keyword);
        List<Goal> goals = goalService.searchGoals(keyword);
        List<GoalDto> result = goals.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/division/{divisionId}/count")
    public ResponseEntity<Long> getGoalCountByDivision(@PathVariable Long divisionId) {
        log.info("GET /goals/division/{}/count - Getting goal count", divisionId);
        long count = goalService.getGoalCountByDivision(divisionId);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<GoalDto> createGoal(@Valid @RequestBody Goal goal) {
        log.info("POST /goals - Creating new goal: {}", goal.getTitle());
        try {
            Goal createdGoal = goalService.createGoal(goal);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(createdGoal));
        } catch (IllegalArgumentException e) {
            log.error("Error creating goal: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GoalDto> updateGoal(@PathVariable Long id, 
                                         @Valid @RequestBody Goal goalDetails) {
        log.info("PUT /goals/{} - Updating goal", id);
        try {
            Goal updatedGoal = goalService.updateGoal(id, goalDetails);
            return ResponseEntity.ok(toDto(updatedGoal));
        } catch (RuntimeException e) {
            log.error("Error updating goal: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        log.info("DELETE /goals/{} - Deleting goal", id);
        try {
            goalService.deleteGoal(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting goal: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    private GoalDto toDto(Goal goal) {
        Long divisionId = goal.getDivision() != null ? goal.getDivision().getId() : null;
        String divisionName = goal.getDivision() != null ? goal.getDivision().getName() : null;
        return new GoalDto(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getTargetValue(),
                goal.getCurrentValue(),
                goal.getDeadline(),
                goal.getProgress() != null ? goal.getProgress() : 0,
                divisionId,
                divisionName
        );
    }
}

