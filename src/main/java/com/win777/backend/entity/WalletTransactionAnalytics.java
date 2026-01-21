package com.win777.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Wallet transaction analytics entity
 */
@Entity
@Table(name = "analytics_wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "transaction_type", length = 50)
    private String transactionType;

    @Builder.Default
    @Column(name = "total_transactions")
    private Integer totalTransactions = 0;

    @Builder.Default
    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "success_count")
    private Integer successCount = 0;

    @Builder.Default
    @Column(name = "failure_count")
    private Integer failureCount = 0;

    @Column(name = "success_rate", precision = 5, scale = 2)
    private BigDecimal successRate;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
