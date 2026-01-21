package com.win777.backend.repository;

import com.win777.backend.entity.SmsMetricsAnalytics;
import com.win777.backend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SmsMetricsAnalytics entity
 */
@Repository
public interface SmsMetricsAnalyticsRepository extends JpaRepository<SmsMetricsAnalytics, Long> {
    
    Optional<SmsMetricsAnalytics> findByBrandAndDate(Brand brand, LocalDate date);
    
    List<SmsMetricsAnalytics> findByBrandAndDateBetweenOrderByDateDesc(Brand brand, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM SmsMetricsAnalytics s WHERE s.brand = ?1 AND s.date >= ?2 ORDER BY s.date DESC")
    List<SmsMetricsAnalytics> findRecentAnalytics(Brand brand, LocalDate since);
}
