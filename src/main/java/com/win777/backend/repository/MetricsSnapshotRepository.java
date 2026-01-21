package com.win777.backend.repository;

import com.win777.backend.entity.MetricsSnapshot;
import com.win777.backend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MetricsSnapshot entity
 */
@Repository
public interface MetricsSnapshotRepository extends JpaRepository<MetricsSnapshot, Long> {
    
    Optional<MetricsSnapshot> findByBrandAndMetricName(Brand brand, String metricName);
    
    List<MetricsSnapshot> findByBrand(Brand brand);
}
