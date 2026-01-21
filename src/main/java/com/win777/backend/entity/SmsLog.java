package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 15)
    private String mobile;
    
    @Column(name = "message_hash", nullable = false, unique = true, length = 64)
    private String messageHash;
    
    @Column(name = "verification_code", length = 10)
    private String verificationCode;
    
    @Column(length = 20)
    private String status = "PENDING";
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
