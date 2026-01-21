package com.win777.backend.repository;

import com.win777.backend.entity.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for CacheConfig entity
 */
@Repository
public interface CacheConfigRepository extends JpaRepository<CacheConfig, Long> {
    
    Optional<CacheConfig> findByCacheKey(String cacheKey);
    
    Optional<CacheConfig> findByCacheKeyAndIsEnabledTrue(String cacheKey);
}
