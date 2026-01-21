package com.win777.backend.repository;

import com.win777.backend.entity.UserBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBanRepository extends JpaRepository<UserBan, Long> {
    
    List<UserBan> findByUserId(Long userId);
    
    Optional<UserBan> findByUserIdAndIsActiveTrue(Long userId);
    
    List<UserBan> findByIsActiveTrue();
    
    boolean existsByUserIdAndIsActiveTrue(Long userId);
}
