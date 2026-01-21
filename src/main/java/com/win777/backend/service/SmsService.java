package com.win777.backend.service;

import com.win777.backend.dto.SmsVerifyRequest;
import com.win777.backend.entity.SmsLog;
import com.win777.backend.repository.SmsLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
public class SmsService {
    
    @Autowired
    private SmsLogRepository smsLogRepository;
    
    @Transactional
    public SmsLog verifyAndLog(SmsVerifyRequest request) {
        String messageHash = generateHash(request.getMessageContent());
        
        if (smsLogRepository.existsByMessageHash(messageHash)) {
            throw new RuntimeException("Duplicate SMS detected");
        }
        
        SmsLog smsLog = SmsLog.builder()
                .userId(request.getUserId())
                .mobile(request.getMobile())
                .messageHash(messageHash)
                .verificationCode(request.getVerificationCode())
                .status("VERIFIED")
                .verifiedAt(LocalDateTime.now())
                .build();
        
        return smsLogRepository.save(smsLog);
    }
    
    private String generateHash(String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
