package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Cache configuration entity
 */
@Entity
@Table(name = "cache_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CacheConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cache_key", unique = true, nullable = false)
    private String cacheKey;

    @Builder.Default
    @Column(name = "ttl_seconds")
    private Integer ttlSeconds = 3600;

    @Builder.Default
    @Column(name = "cache_type", length = 50)
    private String cacheType = "REDIS";

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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
