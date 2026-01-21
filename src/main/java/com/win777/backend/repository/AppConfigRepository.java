package com.win777.backend.repository;

import com.win777.backend.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {
    
    Optional<AppConfig> findByConfigKey(String configKey);
    
    List<AppConfig> findByIsActiveTrue();
    
    List<AppConfig> findByConfigType(String configType);
    
    boolean existsByConfigKey(String configKey);
}
