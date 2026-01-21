package com.win777.backend.service;

import com.win777.backend.entity.AdminAuditLog;
import com.win777.backend.entity.AdminUser;
import com.win777.backend.entity.Task;
import com.win777.backend.entity.User;
import com.win777.backend.entity.Withdrawal;
import com.win777.backend.repository.AdminAuditLogRepository;
import com.win777.backend.repository.AdminUserRepository;
import com.win777.backend.repository.TaskRepository;
import com.win777.backend.repository.UserRepository;
import com.win777.backend.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminUserRepository adminUserRepository;
    
    @Autowired
    private AdminAuditLogRepository auditLogRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private WithdrawalRepository withdrawalRepository;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private FraudPreventionService fraudPreventionService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Authenticate admin user
     */
    public Optional<AdminUser> authenticate(String username, String password) {
        Optional<AdminUser> admin = adminUserRepository.findByUsername(username);
        
        if (admin.isPresent() && admin.get().getIsActive()) {
            if (passwordEncoder.matches(password, admin.get().getPasswordHash())) {
                return admin;
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Create admin user
     */
    @Transactional
    public AdminUser createAdmin(String username, String password, String email, String role) {
        if (adminUserRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (adminUserRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        AdminUser admin = AdminUser.builder()
            .username(username)
            .passwordHash(passwordEncoder.encode(password))
            .email(email)
            .role(role)
            .isActive(true)
            .build();
        
        return adminUserRepository.save(admin);
    }
    
    /**
     * Get all users with pagination
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    /**
     * Search users by mobile
     */
    public List<User> searchUsersByMobile(String mobile) {
        return userRepository.findByMobileContaining(mobile);
    }
    
    /**
     * Ban user (admin action)
     */
    @Transactional
    public void banUser(Long adminId, Long userId, String reason, String ipAddress) {
        fraudPreventionService.banUser(userId, adminId, reason);
        
        logAuditAction(adminId, "BAN_USER", "USER", userId, 
            Map.of("reason", reason), ipAddress);
    }
    
    /**
     * Unban user (admin action)
     */
    @Transactional
    public void unbanUser(Long adminId, Long userId, String ipAddress) {
        fraudPreventionService.unbanUser(userId);
        
        logAuditAction(adminId, "UNBAN_USER", "USER", userId, null, ipAddress);
    }
    
    /**
     * Create or update task
     */
    @Transactional
    public Task saveTask(Long adminId, Task task, String ipAddress) {
        Task savedTask = taskRepository.save(task);
        
        logAuditAction(adminId, "SAVE_TASK", "TASK", savedTask.getId(), 
            Map.of("title", task.getTitle(), "reward", task.getRewardAmount()), ipAddress);
        
        return savedTask;
    }
    
    /**
     * Delete task
     */
    @Transactional
    public void deleteTask(Long adminId, Long taskId, String ipAddress) {
        taskRepository.deleteById(taskId);
        
        logAuditAction(adminId, "DELETE_TASK", "TASK", taskId, null, ipAddress);
    }
    
    /**
     * Approve withdrawal request
     */
    @Transactional
    public Withdrawal approveWithdrawal(Long adminId, Long withdrawalId, String ipAddress) {
        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
            .orElseThrow(() -> new RuntimeException("Withdrawal not found"));
        
        if (!"PENDING".equals(withdrawal.getStatus())) {
            throw new RuntimeException("Withdrawal already processed");
        }
        
        withdrawal.setStatus("APPROVED");
        withdrawal.setApprovedBy(adminId);
        withdrawal.setApprovedAt(LocalDateTime.now());
        withdrawal.setProcessedAt(LocalDateTime.now());
        
        withdrawal = withdrawalRepository.save(withdrawal);
        
        logAuditAction(adminId, "APPROVE_WITHDRAWAL", "WITHDRAWAL", withdrawalId, 
            Map.of("amount", withdrawal.getAmount(), "userId", withdrawal.getUserId()), ipAddress);
        
        return withdrawal;
    }
    
    /**
     * Reject withdrawal request
     */
    @Transactional
    public Withdrawal rejectWithdrawal(Long adminId, Long withdrawalId, String reason, String ipAddress) {
        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
            .orElseThrow(() -> new RuntimeException("Withdrawal not found"));
        
        if (!"PENDING".equals(withdrawal.getStatus())) {
            throw new RuntimeException("Withdrawal already processed");
        }
        
        withdrawal.setStatus("REJECTED");
        withdrawal.setRejectionReason(reason);
        withdrawal.setProcessedAt(LocalDateTime.now());
        
        // Refund the amount back to user's wallet
        walletService.addTransaction(
            withdrawal.getUserId(),
            "REFUND",
            withdrawal.getAmount(),
            "WITHDRAWAL",
            withdrawal.getId(),
            "Withdrawal rejected: " + reason
        );
        
        withdrawal = withdrawalRepository.save(withdrawal);
        
        logAuditAction(adminId, "REJECT_WITHDRAWAL", "WITHDRAWAL", withdrawalId, 
            Map.of("amount", withdrawal.getAmount(), "userId", withdrawal.getUserId(), "reason", reason), 
            ipAddress);
        
        return withdrawal;
    }
    
    /**
     * Get pending withdrawals
     */
    public List<Withdrawal> getPendingWithdrawals() {
        return withdrawalRepository.findByStatus("PENDING");
    }
    
    /**
     * Adjust user wallet balance manually
     */
    @Transactional
    public void adjustUserBalance(Long adminId, Long userId, java.math.BigDecimal amount, 
                                  String reason, String ipAddress) {
        walletService.addTransaction(
            userId,
            "ADMIN_ADJUSTMENT",
            amount,
            "ADMIN",
            adminId,
            reason
        );
        
        logAuditAction(adminId, "ADJUST_BALANCE", "USER", userId, 
            Map.of("amount", amount, "reason", reason), ipAddress);
    }
    
    /**
     * Log admin action for audit trail
     */
    @Transactional
    public void logAuditAction(Long adminId, String action, String targetType, 
                              Long targetId, Map<String, Object> details, String ipAddress) {
        AdminAuditLog log = AdminAuditLog.builder()
            .adminId(adminId)
            .action(action)
            .targetType(targetType)
            .targetId(targetId)
            .details(details != null ? details : new HashMap<>())
            .ipAddress(ipAddress)
            .build();
        
        auditLogRepository.save(log);
    }
    
    /**
     * Get audit logs for admin
     */
    public List<AdminAuditLog> getAuditLogs(Long adminId) {
        return auditLogRepository.findByAdminId(adminId);
    }
    
    /**
     * Get recent audit logs
     */
    public List<AdminAuditLog> getRecentAuditLogs(LocalDateTime since) {
        return auditLogRepository.findByPerformedAtAfter(since);
    }
}
