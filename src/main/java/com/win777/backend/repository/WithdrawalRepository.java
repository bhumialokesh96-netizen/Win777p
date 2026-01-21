package com.win777.backend.repository;

import com.win777.backend.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findByUserId(Long userId);
    List<Withdrawal> findByStatus(String status);
    
    // Analytics queries
    long countByStatus(String status);
}
