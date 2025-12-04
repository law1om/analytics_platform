package com.bankanalytics.repository;

import com.bankanalytics.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {
    
    @Query("SELECT d FROM Division d WHERE d.name LIKE %:name%")
    List<Division> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.division.id = :divisionId")
    long countUsersByDivisionId(@Param("divisionId") Long divisionId);
    
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.division.id = :divisionId")
    long countGoalsByDivisionId(@Param("divisionId") Long divisionId);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.division.id = :divisionId")
    long countReportsByDivisionId(@Param("divisionId") Long divisionId);
}
