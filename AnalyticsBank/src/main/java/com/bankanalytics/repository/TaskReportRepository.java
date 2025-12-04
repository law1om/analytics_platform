package com.bankanalytics.repository;

import com.bankanalytics.entity.TaskReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TaskReportRepository extends JpaRepository<TaskReport, Long> {
    
    List<TaskReport> findByTaskId(Long taskId);
    
    List<TaskReport> findByReportId(Long reportId);
    
    @Query("SELECT tr FROM TaskReport tr WHERE tr.task.id = :taskId AND tr.report.id = :reportId")
    List<TaskReport> findByTaskIdAndReportId(@Param("taskId") Long taskId, 
                                           @Param("reportId") Long reportId);
    
    @Query("SELECT tr FROM TaskReport tr WHERE tr.progress >= :minProgress")
    List<TaskReport> findByProgressGreaterThanEqual(@Param("minProgress") BigDecimal minProgress);
    
    @Query("SELECT COUNT(tr) FROM TaskReport tr WHERE tr.task.id = :taskId")
    long countByTaskId(@Param("taskId") Long taskId);
    
    @Query("SELECT COUNT(tr) FROM TaskReport tr WHERE tr.report.id = :reportId")
    long countByReportId(@Param("reportId") Long reportId);
    
    @Query("SELECT AVG(tr.progress) FROM TaskReport tr WHERE tr.task.id = :taskId")
    BigDecimal getAverageProgressByTaskId(@Param("taskId") Long taskId);
    
    @Query("SELECT tr FROM TaskReport tr WHERE tr.notes LIKE %:keyword%")
    List<TaskReport> findByNotesContaining(@Param("keyword") String keyword);
}
