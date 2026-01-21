package com.win777.backend.service;

import com.win777.backend.entity.DeviceMapping;
import com.win777.backend.entity.FraudLog;
import com.win777.backend.entity.User;
import com.win777.backend.entity.UserBan;
import com.win777.backend.repository.DeviceMappingRepository;
import com.win777.backend.repository.FraudLogRepository;
import com.win777.backend.repository.UserBanRepository;
import com.win777.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class FraudPreventionService {
    
    @Autowired
    private DeviceMappingRepository deviceMappingRepository;
    
    @Autowired
    private FraudLogRepository fraudLogRepository;
    
    @Autowired
    private UserBanRepository userBanRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * Validate device-to-account mapping to prevent multiple accounts on same device
     */
    @Transactional
    public void validateDeviceMapping(Long userId, String deviceFingerprint) {
        Optional<DeviceMapping> existingMapping = deviceMappingRepository.findByDeviceFingerprint(deviceFingerprint);
        
        if (existingMapping.isPresent()) {
            DeviceMapping mapping = existingMapping.get();
            
            // Check if device is already mapped to a different user
            if (!mapping.getUserId().equals(userId)) {
                // Log fraud attempt
                logFraud(userId, deviceFingerprint, "DEVICE_REUSE", 
                    "Device already mapped to user " + mapping.getUserId(), "HIGH");
                throw new RuntimeException("Device already registered to another account");
            }
            
            // Update last seen time
            mapping.setLastSeenAt(LocalDateTime.now());
            deviceMappingRepository.save(mapping);
        } else {
            // Create new device mapping
            DeviceMapping newMapping = DeviceMapping.builder()
                .deviceFingerprint(deviceFingerprint)
                .userId(userId)
                .firstSeenAt(LocalDateTime.now())
                .lastSeenAt(LocalDateTime.now())
                .isSuspicious(false)
                .build();
            deviceMappingRepository.save(newMapping);
        }
        
        // Update device count for the user
        long deviceCount = deviceMappingRepository.countByUserId(userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeviceCount((int) deviceCount);
        userRepository.save(user);
    }
    
    /**
     * Check if SMS rate limit has been exceeded using Redis
     */
    public boolean checkSmsRateLimit(Long userId) {
        String key = "sms:rate:" + userId;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == 1) {
            // Set expiration on first increment (1 hour window)
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
        
        // Maximum 10 SMS verifications per hour
        if (count > 10) {
            logFraud(userId, null, "SMS_RATE_LIMIT_EXCEEDED", 
                "User exceeded SMS rate limit: " + count + " attempts", "MEDIUM");
            return false;
        }
        
        return true;
    }
    
    /**
     * Simple emulator detection heuristics
     */
    public boolean detectEmulator(String deviceFingerprint, Map<String, String> deviceInfo) {
        if (deviceFingerprint == null || deviceInfo == null) {
            return false;
        }
        
        // Common emulator indicators
        String[] emulatorIndicators = {
            "generic", "unknown", "emulator", "sdk", "vbox", "genymotion",
            "andy", "nox", "bluestacks", "memu"
        };
        
        String deviceFingerprintLower = deviceFingerprint.toLowerCase();
        for (String indicator : emulatorIndicators) {
            if (deviceFingerprintLower.contains(indicator)) {
                return true;
            }
        }
        
        // Check device info for emulator patterns
        if (deviceInfo.containsKey("model")) {
            String model = deviceInfo.get("model").toLowerCase();
            for (String indicator : emulatorIndicators) {
                if (model.contains(indicator)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Detect multi-SIM usage patterns
     */
    @Transactional
    public void checkMultiSimPattern(Long userId, String mobile) {
        // Check if user has used multiple different phone numbers
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getMobile().equals(mobile)) {
            logFraud(userId, null, "MULTI_SIM_DETECTED", 
                "User attempting to use different mobile number: " + mobile, "MEDIUM");
            throw new RuntimeException("Mobile number mismatch detected");
        }
    }
    
    /**
     * Check withdrawal cooldown period
     */
    public boolean checkWithdrawalCooldown(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getLastWithdrawalAt() != null) {
            LocalDateTime cooldownEnd = user.getLastWithdrawalAt().plusHours(24);
            if (LocalDateTime.now().isBefore(cooldownEnd)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Update last withdrawal timestamp
     */
    @Transactional
    public void updateLastWithdrawal(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastWithdrawalAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    /**
     * Ban user manually (admin action)
     */
    @Transactional
    public void banUser(Long userId, Long bannedBy, String reason) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsBanned(true);
        user.setBanReason(reason);
        userRepository.save(user);
        
        UserBan ban = UserBan.builder()
            .userId(userId)
            .bannedBy(bannedBy)
            .reason(reason)
            .banType("MANUAL")
            .isActive(true)
            .build();
        
        userBanRepository.save(ban);
        
        logFraud(userId, null, "USER_BANNED", 
            "User banned by admin " + bannedBy + ": " + reason, "CRITICAL");
    }
    
    /**
     * Unban user (admin action)
     */
    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsBanned(false);
        user.setBanReason(null);
        userRepository.save(user);
        
        // Deactivate active bans
        Optional<UserBan> activeBan = userBanRepository.findByUserIdAndIsActiveTrue(userId);
        if (activeBan.isPresent()) {
            UserBan ban = activeBan.get();
            ban.setIsActive(false);
            ban.setUnbannedAt(LocalDateTime.now());
            userBanRepository.save(ban);
        }
    }
    
    /**
     * Check if user is currently banned
     */
    public boolean isUserBanned(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && Boolean.TRUE.equals(user.getIsBanned());
    }
    
    /**
     * Log fraud activity
     */
    @Transactional
    public void logFraud(Long userId, String deviceFingerprint, String fraudType, 
                        String description, String severity) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("timestamp", LocalDateTime.now().toString());
        
        FraudLog log = FraudLog.builder()
            .userId(userId)
            .deviceFingerprint(deviceFingerprint)
            .fraudType(fraudType)
            .description(description)
            .severity(severity)
            .metadata(metadata)
            .build();
        
        fraudLogRepository.save(log);
    }
}
