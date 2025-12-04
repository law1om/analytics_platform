package com.bankanalytics.controller;

import com.bankanalytics.dto.DivisionDto;
import com.bankanalytics.service.DivisionService;
import com.bankanalytics.entity.Division;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/divisions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DivisionController {
    
    private final DivisionService divisionService;
    
    @GetMapping
    public ResponseEntity<List<DivisionDto>> getAllDivisions() {
        log.info("GET /divisions - Fetching all divisions");
        List<Division> divisions = divisionService.getAllDivisions();
        List<DivisionDto> result = divisions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DivisionDto> getDivisionById(@PathVariable Long id) {
        log.info("GET /divisions/{} - Fetching division by id", id);
        return divisionService.getDivisionById(id)
                .map(division -> ResponseEntity.ok(toDto(division)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<DivisionDto>> searchDivisions(@RequestParam String name) {
        log.info("GET /divisions/search?name={} - Searching divisions", name);
        List<Division> divisions = divisionService.searchDivisionsByName(name);
        List<DivisionDto> result = divisions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}/users/count")
    public ResponseEntity<Long> getUserCountByDivision(@PathVariable Long id) {
        log.info("GET /divisions/{}/users/count - Getting user count", id);
        long count = divisionService.getUserCountByDivision(id);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{id}/goals/count")
    public ResponseEntity<Long> getGoalCountByDivision(@PathVariable Long id) {
        log.info("GET /divisions/{}/goals/count - Getting goal count", id);
        long count = divisionService.getGoalCountByDivision(id);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{id}/reports/count")
    public ResponseEntity<Long> getReportCountByDivision(@PathVariable Long id) {
        log.info("GET /divisions/{}/reports/count - Getting report count", id);
        long count = divisionService.getReportCountByDivision(id);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping
    public ResponseEntity<DivisionDto> createDivision(@Valid @RequestBody Division division) {
        log.info("POST /divisions - Creating new division: {}", division.getName());
        Division createdDivision = divisionService.createDivision(division);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(createdDivision));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DivisionDto> updateDivision(@PathVariable Long id, 
                                                 @Valid @RequestBody Division divisionDetails) {
        log.info("PUT /divisions/{} - Updating division", id);
        try {
            Division updatedDivision = divisionService.updateDivision(id, divisionDetails);
            return ResponseEntity.ok(toDto(updatedDivision));
        } catch (RuntimeException e) {
            log.error("Error updating division: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDivision(@PathVariable Long id) {
        log.info("DELETE /divisions/{} - Deleting division", id);
        try {
            divisionService.deleteDivision(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            log.error("Cannot delete division: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Error deleting division: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    private DivisionDto toDto(Division division) {
        return new DivisionDto(
                division.getId(),
                division.getName(),
                division.getBlocks()
        );
    }
}

