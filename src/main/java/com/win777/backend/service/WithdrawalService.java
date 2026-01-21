package com.win777.backend.service;

import com.win777.backend.dto.WithdrawRequest;
import com.win777.backend.entity.Withdrawal;
import com.win777.backend.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WithdrawalService {
    
    @Autowired
    private WithdrawalRepository withdrawalRepository;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private FraudPreventionService fraudPreventionService;
    
    @Transactional
    public Withdrawal requestWithdrawal(Long userId, WithdrawRequest request) {
        // Check if user is banned
        if (fraudPreventionService.isUserBanned(userId)) {
            throw new RuntimeException("Account is banned. Cannot process withdrawal.");
        }
        
        // Check withdrawal cooldown period
        if (!fraudPreventionService.checkWithdrawalCooldown(userId)) {
            throw new RuntimeException("Withdrawal cooldown period active. Please try again later.");
        }
        
        String lockKey = "withdrawal:lock:" + userId;
        
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 30, TimeUnit.SECONDS);
        
        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new RuntimeException("Another withdrawal is in progress");
        }
        
        try {
            BigDecimal currentBalance = walletService.getWalletSummary(userId).getBalance();
            
            if (currentBalance.compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            
            Withdrawal withdrawal = Withdrawal.builder()
                    .userId(userId)
                    .amount(request.getAmount())
                    .status("PENDING")
                    .requestData(request.getAdditionalData())
                    .build();
            
            withdrawal = withdrawalRepository.save(withdrawal);
            
            walletService.addTransaction(
                userId, 
                "WITHDRAWAL", 
                request.getAmount().negate(), 
                "WITHDRAWAL", 
                withdrawal.getId(), 
                "Withdrawal request"
            );
            
            // Update last withdrawal timestamp
            fraudPreventionService.updateLastWithdrawal(userId);
            
            return withdrawal;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
    
    @Transactional
    public Withdrawal processWithdrawal(Long withdrawalId, String newStatus) {
        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new RuntimeException("Withdrawal not found"));
        
        withdrawal.setStatus(newStatus);
        withdrawal.setProcessedAt(LocalDateTime.now());
        
        return withdrawalRepository.save(withdrawal);
    }
    
    public List<Withdrawal> getUserWithdrawals(Long userId) {
        return withdrawalRepository.findByUserId(userId);
    }
}
