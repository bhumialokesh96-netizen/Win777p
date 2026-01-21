package com.win777.backend.service;

import com.win777.backend.dto.WalletSummary;
import com.win777.backend.entity.WalletLedger;
import com.win777.backend.repository.WalletLedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {
    
    @Autowired
    private WalletLedgerRepository walletLedgerRepository;
    
    public WalletSummary getWalletSummary(Long userId) {
        BigDecimal balance = walletLedgerRepository.calculateBalance(userId);
        List<WalletLedger> ledger = walletLedgerRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return new WalletSummary(userId, balance, (long) ledger.size());
    }
    
    public List<WalletLedger> getWalletLedger(Long userId) {
        return walletLedgerRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Transactional
    public WalletLedger addTransaction(Long userId, String transactionType, BigDecimal amount, 
                                       String referenceType, Long referenceId, String description) {
        WalletLedger entry = WalletLedger.builder()
                .userId(userId)
                .transactionType(transactionType)
                .amount(amount)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .description(description)
                .build();
        
        return walletLedgerRepository.save(entry);
    }
}
