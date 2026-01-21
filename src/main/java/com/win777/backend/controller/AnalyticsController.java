package com.win777.backend.controller;

import com.win777.backend.entity.*;
import com.win777.backend.service.AnalyticsService;
import com.win777.backend.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for analytics and metrics APIs
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final BrandService brandService;

    /**
     * Get task engagement analytics
     */
    @GetMapping("/{brandCode}/task-engagement")
    public ResponseEntity<List<TaskEngagementAnalytics>> getTaskEngagementAnalytics(
            @PathVariable String brandCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    List<TaskEngagementAnalytics> analytics = analyticsService.getTaskEngagementAnalytics(brand, startDate, endDate);
                    return ResponseEntity.ok(analytics);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get wallet transaction analytics
     */
    @GetMapping("/{brandCode}/wallet-transactions")
    public ResponseEntity<List<WalletTransactionAnalytics>> getWalletTransactionAnalytics(
            @PathVariable String brandCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    List<WalletTransactionAnalytics> analytics = analyticsService.getWalletTransactionAnalytics(brand, startDate, endDate);
                    return ResponseEntity.ok(analytics);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user growth analytics
     */
    @GetMapping("/{brandCode}/user-growth")
    public ResponseEntity<List<UserGrowthAnalytics>> getUserGrowthAnalytics(
            @PathVariable String brandCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    List<UserGrowthAnalytics> analytics = analyticsService.getUserGrowthAnalytics(brand, startDate, endDate);
                    return ResponseEntity.ok(analytics);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get SMS metrics analytics
     */
    @GetMapping("/{brandCode}/sms-metrics")
    public ResponseEntity<List<SmsMetricsAnalytics>> getSmsMetricsAnalytics(
            @PathVariable String brandCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    List<SmsMetricsAnalytics> analytics = analyticsService.getSmsMetricsAnalytics(brand, startDate, endDate);
                    return ResponseEntity.ok(analytics);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get metrics snapshot (for real-time dashboard)
     */
    @GetMapping("/{brandCode}/snapshot")
    public ResponseEntity<Map<String, BigDecimal>> getMetricsSnapshot(@PathVariable String brandCode) {
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    Map<String, BigDecimal> snapshot = analyticsService.getMetricsSnapshot(brand);
                    return ResponseEntity.ok(snapshot);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get default brand analytics (for backward compatibility)
     */
    @GetMapping("/task-engagement")
    public ResponseEntity<List<TaskEngagementAnalytics>> getDefaultTaskEngagementAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Brand defaultBrand = brandService.getDefaultBrand();
        List<TaskEngagementAnalytics> analytics = analyticsService.getTaskEngagementAnalytics(defaultBrand, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/wallet-transactions")
    public ResponseEntity<List<WalletTransactionAnalytics>> getDefaultWalletTransactionAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Brand defaultBrand = brandService.getDefaultBrand();
        List<WalletTransactionAnalytics> analytics = analyticsService.getWalletTransactionAnalytics(defaultBrand, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/user-growth")
    public ResponseEntity<List<UserGrowthAnalytics>> getDefaultUserGrowthAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Brand defaultBrand = brandService.getDefaultBrand();
        List<UserGrowthAnalytics> analytics = analyticsService.getUserGrowthAnalytics(defaultBrand, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/sms-metrics")
    public ResponseEntity<List<SmsMetricsAnalytics>> getDefaultSmsMetricsAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Brand defaultBrand = brandService.getDefaultBrand();
        List<SmsMetricsAnalytics> analytics = analyticsService.getSmsMetricsAnalytics(defaultBrand, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/snapshot")
    public ResponseEntity<Map<String, BigDecimal>> getDefaultMetricsSnapshot() {
        Brand defaultBrand = brandService.getDefaultBrand();
        Map<String, BigDecimal> snapshot = analyticsService.getMetricsSnapshot(defaultBrand);
        return ResponseEntity.ok(snapshot);
    }

    /**
     * Manually trigger analytics collection (Admin only)
     */
    @PostMapping("/admin/collect")
    public ResponseEntity<String> collectAnalytics() {
        analyticsService.collectDailyAnalytics();
        return ResponseEntity.ok("Analytics collection triggered successfully");
    }

    /**
     * Update metrics snapshot (Admin only)
     */
    @PostMapping("/admin/{brandCode}/update-snapshot")
    public ResponseEntity<String> updateMetricsSnapshot(@PathVariable String brandCode) {
        return brandService.getBrandByCode(brandCode)
                .map(brand -> {
                    analyticsService.updateMetricsSnapshot(brand);
                    return ResponseEntity.ok("Metrics snapshot updated successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
