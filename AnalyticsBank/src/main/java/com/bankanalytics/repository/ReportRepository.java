package com.bankanalytics.repository;

import com.bankanalytics.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByUserId(Long userId);
    
    List<Report> findByDivisionId(Long divisionId);
    
    List<Report> findByReportDate(LocalDate reportDate);
    
    List<Report> findByReportDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT r FROM Report r WHERE r.user.id = :userId AND r.division.id = :divisionId")
    List<Report> findByUserIdAndDivisionId(@Param("userId") Long userId, 
                                          @Param("divisionId") Long divisionId);
    
    @Query("SELECT r FROM Report r WHERE r.division.id = :divisionId AND r.reportDate BETWEEN :startDate AND :endDate")
    List<Report> findByDivisionIdAndReportDateBetween(@Param("divisionId") Long divisionId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.division.id = :divisionId")
    long countByDivisionId(@Param("divisionId") Long divisionId);
    
    @Query("SELECT r FROM Report r WHERE r.title LIKE %:keyword% OR r.content LIKE %:keyword%")
    List<Report> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT r FROM Report r ORDER BY r.reportDate DESC")
    List<Report> findAllOrderByReportDateDesc();
}
