package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    List<Branch> findByGymIdAndActiveTrue(UUID gymId);
}
