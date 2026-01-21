package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_bans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserBan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "banned_by")
    private Long bannedBy;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "ban_type", length = 20)
    private String banType = "MANUAL";
    
    @CreatedDate
    @Column(name = "banned_at")
    private LocalDateTime bannedAt;
    
    @Column(name = "unbanned_at")
    private LocalDateTime unbannedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
}
