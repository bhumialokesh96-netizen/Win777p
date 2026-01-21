package com.win777.backend.repository;

import com.win777.backend.entity.UserGrowthAnalytics;
import com.win777.backend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserGrowthAnalytics entity
 */
@Repository
public interface UserGrowthAnalyticsRepository extends JpaRepository<UserGrowthAnalytics, Long> {
    
    Optional<UserGrowthAnalytics> findByBrandAndDate(Brand brand, LocalDate date);
    
    List<UserGrowthAnalytics> findByBrandAndDateBetweenOrderByDateDesc(Brand brand, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT u FROM UserGrowthAnalytics u WHERE u.brand = ?1 AND u.date >= ?2 ORDER BY u.date DESC")
    List<UserGrowthAnalytics> findRecentAnalytics(Brand brand, LocalDate since);
}
