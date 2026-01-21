package com.win777.backend.controller;

import com.win777.backend.dto.WalletSummary;
import com.win777.backend.entity.WalletLedger;
import com.win777.backend.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    @GetMapping("/summary")
    public ResponseEntity<WalletSummary> getSummary(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        WalletSummary summary = walletService.getWalletSummary(userId);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/ledger")
    public ResponseEntity<List<WalletLedger>> getLedger(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<WalletLedger> ledger = walletService.getWalletLedger(userId);
        return ResponseEntity.ok(ledger);
    }
}
