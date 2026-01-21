package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * SMS metrics analytics entity
 */
@Entity
@Table(name = "analytics_sms_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsMetricsAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Builder.Default
    @Column(name = "total_sms_sent")
    private Integer totalSmsSent = 0;

    @Builder.Default
    @Column(name = "verified_count")
    private Integer verifiedCount = 0;

    @Builder.Default
    @Column(name = "failed_count")
    private Integer failedCount = 0;

    @Column(name = "verification_rate", precision = 5, scale = 2)
    private BigDecimal verificationRate;

    @Builder.Default
    @Column(name = "unique_users")
    private Integer uniqueUsers = 0;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
