package com.win777.backend.controller;

import com.win777.backend.dto.*;
import com.win777.backend.entity.AdminUser;
import com.win777.backend.entity.Task;
import com.win777.backend.entity.User;
import com.win777.backend.entity.Withdrawal;
import com.win777.backend.security.JwtUtil;
import com.win777.backend.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Admin login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request) {
        Optional<AdminUser> admin = adminService.authenticate(request.getUsername(), request.getPassword());
        
        if (admin.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        
        AdminUser adminUser = admin.get();
        
        // Generate JWT token for admin
        String token = jwtUtil.generateToken(adminUser.getUsername());
        
        AdminLoginResponse response = AdminLoginResponse.builder()
            .adminId(adminUser.getId())
            .username(adminUser.getUsername())
            .email(adminUser.getEmail())
            .role(adminUser.getRole())
            .token(token)
            .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all users with pagination
     */
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }
    
    /**
     * Search users by mobile
     */
    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String mobile) {
        return ResponseEntity.ok(adminService.searchUsersByMobile(mobile));
    }
    
    /**
     * Ban user
     */
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<?> banUser(
            @PathVariable Long userId,
            @Valid @RequestBody BanUserRequest request,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest) {
        
        Long adminId = getAdminIdFromToken(authHeader);
        String ipAddress = getClientIpAddress(httpRequest);
        
        adminService.banUser(adminId, userId, request.getReason(), ipAddress);
        return ResponseEntity.ok("User banned successfully");
    }
    
    /**
     * Unban user
     */
    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<?> unbanUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest) {
        
        Long adminId = getAdminIdFromToken(authHeader);
        String ipAddress = getClientIpAddress(httpRequest);
        
        adminService.unbanUser(adminId, userId, ipAddress);
        return ResponseEntity.ok("User unbanned successfully");
    }
    
    /**
     * Create or update task
     */
    @PostMapping("/tasks")
    public ResponseEntity<Task> saveTask(
            @RequestBody Task task,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest) {
        
        Long adminId = getAdminIdFromToken(authHeader);
        String ipAddress = getClientIpAddress(httpRequest);
        
        Task savedTask = adminService.saveTask(adminId, task, ipAddress);
        return ResponseEntity.ok(savedTask);
    }
    
    /**
     * Delete task
     */
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long taskId,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest) {
        
        Long adminId = getAdminIdFromToken(authHeader);
        String ipAddress = getClientIpAddress(httpRequest);
        
        adminService.deleteTask(adminId, taskId, ipAddress);
        return ResponseEntity.ok("Task deleted successfully");
    }
    
    /**
     * Get pending withdrawals
     */
    @GetMapping("/withdrawals/pending")
    public ResponseEntity<List<Withdrawal>> getPendingWithdrawals() {
        return ResponseEntity.ok(adminService.getPendingWithdrawals());
    }
    
    /**
     * Approve withdrawal
     */
    @PostMapping("/withdrawals/{withdrawalId}/approve")
    public ResponseEntity<Withdrawal> approveWithdrawal(
            @PathVariable Long withdrawalId,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest) {
        
        Long adminId = getAdminIdFromToken(authHeader);
        String ipAddress = getClientIpAddress(httpRequest);
        
        Withdrawal withdrawal = adminService.approveWithdrawal(adminId, withdrawalId, ipAddress);
        return ResponseEntity.ok(withdrawal);
    }
    
    /**
     * Reject withdrawal
     */
    @PostMapping("/withdrawals/{withdrawalId}/reject")
    public ResponseEntity<Withdrawal> rejectWithdrawal(
            @PathVariable Long withdrawalId,
            @Valid @RequestBody RejectWithdrawalRequest request,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest) {
        
        Long adminId = getAdminIdFromToken(authHeader);
        String ipAddress = getClientIpAddress(httpRequest);
        
        Withdrawal withdrawal = adminService.rejectWithdrawal(adminId, withdrawalId, 
            request.getReason(), ipAddress);
        return ResponseEntity.ok(withdrawal);
    }
    
    /**
     * Adjust user balance
     */
    @PostMapping("/users/{userId}/adjust-balance")
    public ResponseEntity<?> adjustBalance(
            @PathVariable Long userId,
            @Valid @RequestBody AdjustBalanceRequest request,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest) {
        
        Long adminId = getAdminIdFromToken(authHeader);
        String ipAddress = getClientIpAddress(httpRequest);
        
        adminService.adjustUserBalance(adminId, userId, request.getAmount(), 
            request.getReason(), ipAddress);
        return ResponseEntity.ok("Balance adjusted successfully");
    }
    
    /**
     * Extract admin ID from JWT token
     */
    private Long getAdminIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        // For now, return a dummy admin ID. In production, you'd fetch the actual admin ID
        return 1L;
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
