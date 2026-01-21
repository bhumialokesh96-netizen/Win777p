package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * System health metrics entity for monitoring
 */
@Entity
@Table(name = "system_health_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemHealthMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_type", nullable = false, length = 50)
    private String metricType;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", precision = 15, scale = 2)
    private BigDecimal metricValue;

    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "HEALTHY";

    @Column(name = "details", columnDefinition = "jsonb")
    private String details;

    @Builder.Default
    @Column(name = "checked_at")
    private LocalDateTime checkedAt = LocalDateTime.now();
}
