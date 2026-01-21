package com.win777.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletSummary {
    private Long userId;
    private BigDecimal balance;
    private Long transactionCount;
}
