package com.win777.backend.repository;

import com.win777.backend.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
    
    List<AdminAuditLog> findByAdminId(Long adminId);
    
    List<AdminAuditLog> findByAction(String action);
    
    List<AdminAuditLog> findByPerformedAtAfter(LocalDateTime dateTime);
    
    List<AdminAuditLog> findByTargetTypeAndTargetId(String targetType, Long targetId);
}
