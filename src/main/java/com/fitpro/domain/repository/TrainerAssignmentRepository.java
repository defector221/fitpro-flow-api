package com.fitpro.domain.repository;

import com.fitpro.domain.entity.TrainerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrainerAssignmentRepository extends JpaRepository<TrainerAssignment, UUID> {
    List<TrainerAssignment> findByTrainerIdAndActiveTrue(UUID trainerId);

    List<TrainerAssignment> findByMemberIdAndActiveTrue(UUID memberId);
}
