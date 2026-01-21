package com.win777.backend.service;

import com.win777.backend.dto.*;
import com.win777.backend.entity.User;
import com.win777.backend.repository.UserRepository;
import com.win777.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private FraudPreventionService fraudPreventionService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new RuntimeException("Mobile number already registered");
        }
        
        // Check for emulator
        Map<String, String> deviceInfo = new HashMap<>();
        boolean isEmulator = fraudPreventionService.detectEmulator(
            request.getDeviceFingerprint(), deviceInfo);
        
        User user = User.builder()
                .mobile(request.getMobile())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .deviceFingerprint(request.getDeviceFingerprint())
                .status("ACTIVE")
                .isEmulator(isEmulator)
                .isBanned(false)
                .deviceCount(1)
                .build();
        
        user = userRepository.save(user);
        
        // Create device mapping
        fraudPreventionService.validateDeviceMapping(user.getId(), request.getDeviceFingerprint());
        
        // Log emulator detection
        if (isEmulator) {
            fraudPreventionService.logFraud(user.getId(), request.getDeviceFingerprint(), 
                "EMULATOR_DETECTED", "Emulator detected during registration", "MEDIUM");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getMobile());
        return new AuthResponse(token, user.getMobile(), user.getId());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByMobile(request.getMobile())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        // Check if user is banned
        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new RuntimeException("Account has been banned: " + user.getBanReason());
        }
        
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("Account is not active");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getMobile());
        return new AuthResponse(token, user.getMobile(), user.getId());
    }
    
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByMobile(request.getMobile())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
