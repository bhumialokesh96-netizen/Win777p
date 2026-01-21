package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "admin_audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AdminAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "admin_id", nullable = false)
    private Long adminId;
    
    @Column(nullable = false, length = 100)
    private String action;
    
    @Column(name = "target_type", length = 50)
    private String targetType;
    
    @Column(name = "target_id")
    private Long targetId;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> details;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @CreatedDate
    @Column(name = "performed_at")
    private LocalDateTime performedAt;
}
