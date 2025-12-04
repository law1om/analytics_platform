package com.bankanalytics.service;

import com.bankanalytics.entity.Division;
import com.bankanalytics.repository.DivisionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DivisionService {
    
    private final DivisionRepository divisionRepository;
    
    public List<Division> getAllDivisions() {
        log.debug("Fetching all divisions");
        return divisionRepository.findAll();
    }
    
    public Optional<Division> getDivisionById(Long id) {
        log.debug("Fetching division by id: {}", id);
        return divisionRepository.findById(id);
    }
    
    public Division createDivision(Division division) {
        log.info("Creating new division: {}", division.getName());
        return divisionRepository.save(division);
    }
    
    public Division updateDivision(Long id, Division divisionDetails) {
        log.info("Updating division with id: {}", id);
        
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Division not found with id: " + id));
        
        division.setName(divisionDetails.getName());
        division.setBlocks(divisionDetails.getBlocks());
        
        return divisionRepository.save(division);
    }
    
    public void deleteDivision(Long id) {
        log.info("Deleting division with id: {}", id);
        
        if (!divisionRepository.existsById(id)) {
            throw new RuntimeException("Division not found with id: " + id);
        }

        long userCount = divisionRepository.countUsersByDivisionId(id);
        long goalCount = divisionRepository.countGoalsByDivisionId(id);
        long reportCount = divisionRepository.countReportsByDivisionId(id);
        
        if (userCount > 0 || goalCount > 0 || reportCount > 0) {
            throw new IllegalStateException("Cannot delete division with associated users, goals, or reports");
        }
        
        divisionRepository.deleteById(id);
    }
    
    public List<Division> searchDivisionsByName(String name) {
        log.debug("Searching divisions by name: {}", name);
        return divisionRepository.findByNameContaining(name);
    }
    
    public long getUserCountByDivision(Long divisionId) {
        log.debug("Getting user count for division: {}", divisionId);
        return divisionRepository.countUsersByDivisionId(divisionId);
    }
    
    public long getGoalCountByDivision(Long divisionId) {
        log.debug("Getting goal count for division: {}", divisionId);
        return divisionRepository.countGoalsByDivisionId(divisionId);
    }
    
    public long getReportCountByDivision(Long divisionId) {
        log.debug("Getting report count for division: {}", divisionId);
        return divisionRepository.countReportsByDivisionId(divisionId);
    }
}
