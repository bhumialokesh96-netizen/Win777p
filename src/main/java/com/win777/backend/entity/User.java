package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 15)
    private String mobile;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "device_fingerprint")
    private String deviceFingerprint;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    
    @Column(length = 20)
    private String status = "ACTIVE";
    
    @Column(name = "is_banned")
    private Boolean isBanned = false;
    
    @Column(name = "ban_reason", columnDefinition = "TEXT")
    private String banReason;
    
    @Column(name = "is_emulator")
    private Boolean isEmulator = false;
    
    @Column(name = "device_count")
    private Integer deviceCount = 1;
    
    @Column(name = "last_withdrawal_at")
    private LocalDateTime lastWithdrawalAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
