package com.win777.backend.repository;

import com.win777.backend.entity.TaskEngagementAnalytics;
import com.win777.backend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TaskEngagementAnalytics entity
 */
@Repository
public interface TaskEngagementAnalyticsRepository extends JpaRepository<TaskEngagementAnalytics, Long> {
    
    Optional<TaskEngagementAnalytics> findByBrandAndTaskIdAndDate(Brand brand, Long taskId, LocalDate date);
    
    List<TaskEngagementAnalytics> findByBrandAndDateBetweenOrderByDateDesc(Brand brand, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT t FROM TaskEngagementAnalytics t WHERE t.brand = ?1 AND t.date >= ?2 ORDER BY t.date DESC")
    List<TaskEngagementAnalytics> findRecentAnalytics(Brand brand, LocalDate since);
}
