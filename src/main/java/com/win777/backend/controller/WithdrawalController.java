package com.win777.backend.controller;

import com.win777.backend.dto.WithdrawRequest;
import com.win777.backend.entity.Withdrawal;
import com.win777.backend.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/withdraw")
public class WithdrawalController {
    
    @Autowired
    private WithdrawalService withdrawalService;
    
    @PostMapping("/request")
    public ResponseEntity<Withdrawal> requestWithdrawal(
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Withdrawal withdrawal = withdrawalService.requestWithdrawal(userId, request);
        return ResponseEntity.ok(withdrawal);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<Withdrawal>> getHistory(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<Withdrawal> withdrawals = withdrawalService.getUserWithdrawals(userId);
        return ResponseEntity.ok(withdrawals);
    }
}
