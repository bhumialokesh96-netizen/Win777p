package com.win777.backend.repository;

import com.win777.backend.entity.DeviceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceMappingRepository extends JpaRepository<DeviceMapping, Long> {
    
    Optional<DeviceMapping> findByDeviceFingerprint(String deviceFingerprint);
    
    List<DeviceMapping> findByUserId(Long userId);
    
    boolean existsByDeviceFingerprint(String deviceFingerprint);
    
    long countByUserId(Long userId);
}
