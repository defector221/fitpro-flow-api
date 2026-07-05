package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Lead;
import com.fitpro.domain.enums.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    Page<Lead> findByGymId(UUID gymId, Pageable pageable);

    long countByGymIdAndStatus(UUID gymId, LeadStatus status);
}
