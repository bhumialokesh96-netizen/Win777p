package com.win777.backend.repository;

import com.win777.backend.entity.FraudLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FraudLogRepository extends JpaRepository<FraudLog, Long> {
    
    List<FraudLog> findByUserId(Long userId);
    
    List<FraudLog> findByFraudType(String fraudType);
    
    List<FraudLog> findByDetectedAtAfter(LocalDateTime dateTime);
    
    List<FraudLog> findByUserIdAndFraudType(Long userId, String fraudType);
}
