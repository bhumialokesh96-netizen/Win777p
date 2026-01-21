package com.win777.backend.repository;

import com.win777.backend.entity.SystemHealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for SystemHealthMetric entity
 */
@Repository
public interface SystemHealthMetricRepository extends JpaRepository<SystemHealthMetric, Long> {
    
    List<SystemHealthMetric> findByMetricTypeOrderByCheckedAtDesc(String metricType);
    
    @Query("SELECT s FROM SystemHealthMetric s WHERE s.checkedAt >= ?1 ORDER BY s.checkedAt DESC")
    List<SystemHealthMetric> findRecentMetrics(LocalDateTime since);
    
    List<SystemHealthMetric> findByStatusOrderByCheckedAtDesc(String status);
}
