package com.win777.backend.repository;

import com.win777.backend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Brand entity
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    
    Optional<Brand> findByBrandCode(String brandCode);
    
    Optional<Brand> findByDomain(String domain);
    
    boolean existsByBrandCode(String brandCode);
}
