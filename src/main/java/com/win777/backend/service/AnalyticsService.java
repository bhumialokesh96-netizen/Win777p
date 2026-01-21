package com.win777.backend.service;

import com.win777.backend.entity.*;
import com.win777.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for analytics data collection and aggregation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final TaskEngagementAnalyticsRepository taskEngagementRepository;
    private final WalletTransactionAnalyticsRepository walletTransactionRepository;
    private final UserGrowthAnalyticsRepository userGrowthRepository;
    private final SmsMetricsAnalyticsRepository smsMetricsRepository;
    private final MetricsSnapshotRepository metricsSnapshotRepository;
    
    private final BrandService brandService;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final WalletLedgerRepository walletLedgerRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final SmsLogRepository smsLogRepository;

    /**
     * Get task engagement analytics for a date range
     */
    @Cacheable(value = "analytics:task", key = "#brand.id + ':' + #startDate + ':' + #endDate")
    public List<TaskEngagementAnalytics> getTaskEngagementAnalytics(Brand brand, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting task engagement analytics for brand: {} from {} to {}", brand.getBrandCode(), startDate, endDate);
        return taskEngagementRepository.findByBrandAndDateBetweenOrderByDateDesc(brand, startDate, endDate);
    }

    /**
     * Get wallet transaction analytics for a date range
     */
    @Cacheable(value = "analytics:wallet", key = "#brand.id + ':' + #startDate + ':' + #endDate")
    public List<WalletTransactionAnalytics> getWalletTransactionAnalytics(Brand brand, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting wallet transaction analytics for brand: {} from {} to {}", brand.getBrandCode(), startDate, endDate);
        return walletTransactionRepository.findByBrandAndDateBetweenOrderByDateDesc(brand, startDate, endDate);
    }

    /**
     * Get user growth analytics for a date range
     */
    @Cacheable(value = "analytics:user", key = "#brand.id + ':' + #startDate + ':' + #endDate")
    public List<UserGrowthAnalytics> getUserGrowthAnalytics(Brand brand, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting user growth analytics for brand: {} from {} to {}", brand.getBrandCode(), startDate, endDate);
        return userGrowthRepository.findByBrandAndDateBetweenOrderByDateDesc(brand, startDate, endDate);
    }

    /**
     * Get SMS metrics analytics for a date range
     */
    @Cacheable(value = "analytics:sms", key = "#brand.id + ':' + #startDate + ':' + #endDate")
    public List<SmsMetricsAnalytics> getSmsMetricsAnalytics(Brand brand, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting SMS metrics analytics for brand: {} from {} to {}", brand.getBrandCode(), startDate, endDate);
        return smsMetricsRepository.findByBrandAndDateBetweenOrderByDateDesc(brand, startDate, endDate);
    }

    /**
     * Get metrics snapshot for dashboard
     */
    @Cacheable(value = "analytics:snapshot", key = "#brand.id")
    public Map<String, BigDecimal> getMetricsSnapshot(Brand brand) {
        log.debug("Getting metrics snapshot for brand: {}", brand.getBrandCode());
        List<MetricsSnapshot> snapshots = metricsSnapshotRepository.findByBrand(brand);
        
        Map<String, BigDecimal> metrics = new HashMap<>();
        for (MetricsSnapshot snapshot : snapshots) {
            metrics.put(snapshot.getMetricName(), snapshot.getMetricValue());
        }
        
        return metrics;
    }

    /**
     * Update task engagement analytics for a specific date
     */
    @Transactional
    public void updateTaskEngagementAnalytics(Brand brand, Long taskId, LocalDate date) {
        log.debug("Updating task engagement analytics for task: {} on {}", taskId, date);
        
        // Get task assignment stats
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<TaskAssignment> assignments = taskAssignmentRepository
                .findByTaskIdAndCreatedAtBetween(taskId, startOfDay, endOfDay);
        
        int totalAssignments = assignments.size();
        int completedCount = (int) assignments.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()))
                .count();
        
        long uniqueUsers = assignments.stream()
                .map(TaskAssignment::getUserId)
                .distinct()
                .count();
        
        BigDecimal completionRate = totalAssignments > 0 
                ? BigDecimal.valueOf(completedCount * 100.0 / totalAssignments).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // Calculate total rewards paid from wallet ledger
        BigDecimal totalRewards = walletLedgerRepository
                .findByTransactionTypeAndCreatedAtBetween("TASK_REWARD", startOfDay, endOfDay)
                .stream()
                .filter(ledger -> assignments.stream()
                        .anyMatch(a -> a.getUserId().equals(ledger.getUserId()) && "COMPLETED".equals(a.getStatus())))
                .map(WalletLedger::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get task entity
        Task task = taskRepository.findById(taskId).orElse(null);
        
        // Update or create analytics record
        TaskEngagementAnalytics analytics = taskEngagementRepository
                .findByBrandAndTaskIdAndDate(brand, taskId, date)
                .orElse(TaskEngagementAnalytics.builder()
                        .brand(brand)
                        .task(task)
                        .date(date)
                        .build());
        
        analytics.setTotalAssignments(totalAssignments);
        analytics.setCompletedCount(completedCount);
        analytics.setCompletionRate(completionRate);
        analytics.setTotalRewardsPaid(totalRewards);
        analytics.setUniqueUsers((int) uniqueUsers);
        
        taskEngagementRepository.save(analytics);
    }

    /**
     * Update user growth analytics for a specific date
     */
    @Transactional
    public void updateUserGrowthAnalytics(Brand brand, LocalDate date) {
        log.debug("Updating user growth analytics for {} on {}", brand.getBrandCode(), date);
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        // Count new users registered on this date
        long newUsers = userRepository.countByBrandAndCreatedAtBetween(brand, startOfDay, endOfDay);
        
        // Count active users (users with activity on this date)
        // This is simplified - in real implementation, track user activity
        long activeUsers = userRepository.countByBrandAndUpdatedAtBetween(brand, startOfDay, endOfDay);
        
        // Count total users up to this date
        long totalUsers = userRepository.countByBrandAndCreatedAtBefore(brand, endOfDay);
        
        // Calculate retention rate (simplified)
        BigDecimal retentionRate = totalUsers > 0 
                ? BigDecimal.valueOf(activeUsers * 100.0 / totalUsers).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // Update or create analytics record
        UserGrowthAnalytics analytics = userGrowthRepository
                .findByBrandAndDate(brand, date)
                .orElse(UserGrowthAnalytics.builder()
                        .brand(brand)
                        .date(date)
                        .build());
        
        analytics.setNewUsers((int) newUsers);
        analytics.setActiveUsers((int) activeUsers);
        analytics.setTotalUsers((int) totalUsers);
        analytics.setRetentionRate(retentionRate);
        
        userGrowthRepository.save(analytics);
    }

    /**
     * Update wallet transaction analytics for a specific date
     */
    @Transactional
    public void updateWalletTransactionAnalytics(Brand brand, LocalDate date, String transactionType) {
        log.debug("Updating wallet transaction analytics for {} on {} - type: {}", brand.getBrandCode(), date, transactionType);
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        // Get wallet transactions for this date and type
        List<WalletLedger> transactions = walletLedgerRepository
                .findByTransactionTypeAndCreatedAtBetween(transactionType, startOfDay, endOfDay);
        
        int totalTransactions = transactions.size();
        BigDecimal totalAmount = transactions.stream()
                .map(WalletLedger::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // For success/failure, we'd need additional status tracking in WalletLedger
        int successCount = totalTransactions; // Simplified
        int failureCount = 0;
        
        BigDecimal successRate = totalTransactions > 0 
                ? BigDecimal.valueOf(successCount * 100.0 / totalTransactions).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // Update or create analytics record
        WalletTransactionAnalytics analytics = walletTransactionRepository
                .findByBrandAndDateAndTransactionType(brand, date, transactionType)
                .orElse(WalletTransactionAnalytics.builder()
                        .brand(brand)
                        .date(date)
                        .transactionType(transactionType)
                        .build());
        
        analytics.setTotalTransactions(totalTransactions);
        analytics.setTotalAmount(totalAmount);
        analytics.setSuccessCount(successCount);
        analytics.setFailureCount(failureCount);
        analytics.setSuccessRate(successRate);
        
        walletTransactionRepository.save(analytics);
    }

    /**
     * Update SMS metrics analytics for a specific date
     */
    @Transactional
    public void updateSmsMetricsAnalytics(Brand brand, LocalDate date) {
        log.debug("Updating SMS metrics analytics for {} on {}", brand.getBrandCode(), date);
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        // Get SMS logs for this date
        List<SmsLog> smsLogs = smsLogRepository.findByCreatedAtBetween(startOfDay, endOfDay);
        
        int totalSms = smsLogs.size();
        int verifiedCount = (int) smsLogs.stream()
                .filter(log -> log.getVerifiedAt() != null)
                .count();
        int failedCount = totalSms - verifiedCount;
        
        long uniqueUsers = smsLogs.stream()
                .map(SmsLog::getUserId)
                .distinct()
                .count();
        
        BigDecimal verificationRate = totalSms > 0 
                ? BigDecimal.valueOf(verifiedCount * 100.0 / totalSms).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // Update or create analytics record
        SmsMetricsAnalytics analytics = smsMetricsRepository
                .findByBrandAndDate(brand, date)
                .orElse(SmsMetricsAnalytics.builder()
                        .brand(brand)
                        .date(date)
                        .build());
        
        analytics.setTotalSmsSent(totalSms);
        analytics.setVerifiedCount(verifiedCount);
        analytics.setFailedCount(failedCount);
        analytics.setVerificationRate(verificationRate);
        analytics.setUniqueUsers((int) uniqueUsers);
        
        smsMetricsRepository.save(analytics);
    }

    /**
     * Update metrics snapshot for real-time dashboard
     */
    @Transactional
    public void updateMetricsSnapshot(Brand brand) {
        log.debug("Updating metrics snapshot for {}", brand.getBrandCode());
        
        // Total users
        long totalUsers = userRepository.countByBrand(brand);
        updateSnapshot(brand, "total_users", BigDecimal.valueOf(totalUsers));
        
        // Active users today
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long activeUsersToday = userRepository.countByBrandAndUpdatedAtAfter(brand, startOfToday);
        updateSnapshot(brand, "active_users_today", BigDecimal.valueOf(activeUsersToday));
        
        // Total transactions
        long totalTransactions = walletLedgerRepository.count();
        updateSnapshot(brand, "total_transactions", BigDecimal.valueOf(totalTransactions));
        
        // Pending withdrawals
        long pendingWithdrawals = withdrawalRepository.countByStatus("PENDING");
        updateSnapshot(brand, "pending_withdrawals", BigDecimal.valueOf(pendingWithdrawals));
        
        // Tasks completed today
        LocalDateTime endOfToday = LocalDate.now().plusDays(1).atStartOfDay();
        long tasksCompletedToday = taskAssignmentRepository
                .countByStatusAndCreatedAtBetween("COMPLETED", startOfToday, endOfToday);
        updateSnapshot(brand, "tasks_completed_today", BigDecimal.valueOf(tasksCompletedToday));
    }

    private void updateSnapshot(Brand brand, String metricName, BigDecimal value) {
        MetricsSnapshot snapshot = metricsSnapshotRepository
                .findByBrandAndMetricName(brand, metricName)
                .orElse(MetricsSnapshot.builder()
                        .brand(brand)
                        .metricName(metricName)
                        .build());
        
        snapshot.setMetricValue(value);
        snapshot.setSnapshotAt(LocalDateTime.now());
        
        metricsSnapshotRepository.save(snapshot);
    }

    /**
     * Scheduled job to collect daily analytics
     * Runs every day at 1 AM
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void collectDailyAnalytics() {
        log.info("Starting daily analytics collection");
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Brand> brands = brandService.getAllActiveBrands();
        
        for (Brand brand : brands) {
            try {
                log.info("Collecting analytics for brand: {}", brand.getBrandCode());
                
                // Update user growth analytics
                updateUserGrowthAnalytics(brand, yesterday);
                
                // Update wallet transaction analytics
                updateWalletTransactionAnalytics(brand, yesterday, "REWARD");
                updateWalletTransactionAnalytics(brand, yesterday, "WITHDRAWAL");
                
                // Update SMS metrics
                updateSmsMetricsAnalytics(brand, yesterday);
                
                // Update metrics snapshot
                updateMetricsSnapshot(brand);
                
                log.info("Analytics collection completed for brand: {}", brand.getBrandCode());
            } catch (Exception e) {
                log.error("Error collecting analytics for brand: {}", brand.getBrandCode(), e);
            }
        }
        
        log.info("Daily analytics collection completed");
    }
}
