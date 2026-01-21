package com.win777.backend.repository;

import com.win777.backend.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    List<TaskAssignment> findByUserId(Long userId);
    Optional<TaskAssignment> findByUserIdAndTaskId(Long userId, Long taskId);
    
    // Analytics queries
    List<TaskAssignment> findByTaskIdAndCreatedAtBetween(Long taskId, LocalDateTime start, LocalDateTime end);
    long countByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);
}
