package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Metrics snapshot entity for real-time dashboard
 */
@Entity
@Table(name = "analytics_metrics_snapshot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", precision = 15, scale = 2)
    private BigDecimal metricValue;

    @Column(name = "metric_metadata", columnDefinition = "jsonb")
    private String metricMetadata;

    @Builder.Default
    @Column(name = "snapshot_at")
    private LocalDateTime snapshotAt = LocalDateTime.now();
}
