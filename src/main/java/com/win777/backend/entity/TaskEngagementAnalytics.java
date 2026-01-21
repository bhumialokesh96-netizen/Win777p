package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Task engagement analytics entity
 */
@Entity
@Table(name = "analytics_task_engagement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEngagementAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Builder.Default
    @Column(name = "total_assignments")
    private Integer totalAssignments = 0;

    @Builder.Default
    @Column(name = "completed_count")
    private Integer completedCount = 0;

    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate;

    @Builder.Default
    @Column(name = "total_rewards_paid", precision = 15, scale = 2)
    private BigDecimal totalRewardsPaid = BigDecimal.ZERO;

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
