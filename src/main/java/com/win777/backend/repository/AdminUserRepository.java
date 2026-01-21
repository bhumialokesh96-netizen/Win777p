package com.win777.backend.repository;

import com.win777.backend.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    Optional<AdminUser> findByUsername(String username);
    
    Optional<AdminUser> findByEmail(String email);
    
    List<AdminUser> findByIsActiveTrue();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
