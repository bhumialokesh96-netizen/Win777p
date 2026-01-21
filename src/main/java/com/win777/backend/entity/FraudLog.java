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
@Table(name = "fraud_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FraudLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "device_fingerprint")
    private String deviceFingerprint;
    
    @Column(name = "fraud_type", nullable = false, length = 50)
    private String fraudType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 20)
    private String severity = "LOW";
    
    @CreatedDate
    @Column(name = "detected_at")
    private LocalDateTime detectedAt;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
