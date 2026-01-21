package com.win777.backend.repository;

import com.win777.backend.entity.WalletTransactionAnalytics;
import com.win777.backend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WalletTransactionAnalytics entity
 */
@Repository
public interface WalletTransactionAnalyticsRepository extends JpaRepository<WalletTransactionAnalytics, Long> {
    
    Optional<WalletTransactionAnalytics> findByBrandAndDateAndTransactionType(Brand brand, LocalDate date, String transactionType);
    
    List<WalletTransactionAnalytics> findByBrandAndDateBetweenOrderByDateDesc(Brand brand, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT w FROM WalletTransactionAnalytics w WHERE w.brand = ?1 AND w.date >= ?2 ORDER BY w.date DESC")
    List<WalletTransactionAnalytics> findRecentAnalytics(Brand brand, LocalDate since);
}
