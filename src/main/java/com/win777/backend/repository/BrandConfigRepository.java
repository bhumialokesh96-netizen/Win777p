package com.win777.backend.repository;

import com.win777.backend.entity.BrandConfig;
import com.win777.backend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BrandConfig entity
 */
@Repository
public interface BrandConfigRepository extends JpaRepository<BrandConfig, Long> {
    
    List<BrandConfig> findByBrandAndIsActiveTrue(Brand brand);
    
    Optional<BrandConfig> findByBrandAndConfigKeyAndIsActiveTrue(Brand brand, String configKey);
    
    List<BrandConfig> findByBrand(Brand brand);
}
