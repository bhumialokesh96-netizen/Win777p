package com.win777.backend.repository;

import com.win777.backend.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
    Optional<SmsLog> findByMessageHash(String messageHash);
    boolean existsByMessageHash(String messageHash);
}
