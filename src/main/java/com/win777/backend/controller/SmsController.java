package com.win777.backend.controller;

import com.win777.backend.dto.SmsVerifyRequest;
import com.win777.backend.entity.SmsLog;
import com.win777.backend.service.SmsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsController {
    
    @Autowired
    private SmsService smsService;
    
    @PostMapping("/verify")
    public ResponseEntity<SmsLog> verify(@Valid @RequestBody SmsVerifyRequest request) {
        SmsLog smsLog = smsService.verifyAndLog(request);
        return ResponseEntity.ok(smsLog);
    }
}
