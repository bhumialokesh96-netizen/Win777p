package com.win777.backend.repository;

import com.win777.backend.entity.User;
import com.win777.backend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
    List<User> findByMobileContaining(String mobile);
    
    // Brand-specific queries
    long countByBrand(Brand brand);
    long countByBrandAndCreatedAtBetween(Brand brand, LocalDateTime start, LocalDateTime end);
    long countByBrandAndUpdatedAtBetween(Brand brand, LocalDateTime start, LocalDateTime end);
    long countByBrandAndCreatedAtBefore(Brand brand, LocalDateTime before);
    long countByBrandAndUpdatedAtAfter(Brand brand, LocalDateTime after);
}
