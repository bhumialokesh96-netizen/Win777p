package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User growth analytics entity
 */
@Entity
@Table(name = "analytics_user_growth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGrowthAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Builder.Default
    @Column(name = "new_users")
    private Integer newUsers = 0;

    @Builder.Default
    @Column(name = "active_users")
    private Integer activeUsers = 0;

    @Builder.Default
    @Column(name = "referred_users")
    private Integer referredUsers = 0;

    @Builder.Default
    @Column(name = "total_users")
    private Integer totalUsers = 0;

    @Column(name = "retention_rate", precision = 5, scale = 2)
    private BigDecimal retentionRate;

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
