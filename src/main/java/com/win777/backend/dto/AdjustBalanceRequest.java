package com.win777.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustBalanceRequest {
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    @NotNull(message = "Reason is required")
    private String reason;
}
