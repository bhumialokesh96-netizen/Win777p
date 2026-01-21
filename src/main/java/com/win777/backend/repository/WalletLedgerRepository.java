package com.win777.backend.repository;

import com.win777.backend.entity.WalletLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WalletLedgerRepository extends JpaRepository<WalletLedger, Long> {
    List<WalletLedger> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT COALESCE(SUM(wl.amount), 0) FROM WalletLedger wl WHERE wl.userId = :userId")
    BigDecimal calculateBalance(@Param("userId") Long userId);
}
