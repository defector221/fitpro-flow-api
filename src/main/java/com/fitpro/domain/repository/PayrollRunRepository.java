package com.fitpro.domain.repository;

import com.fitpro.domain.entity.PayrollRun;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, UUID> {
    Page<PayrollRun> findByGymIdOrderByCreatedAtDesc(UUID gymId, Pageable pageable);

    Optional<PayrollRun> findByGymIdAndPeriodMonthAndPeriodYear(UUID gymId, int month, int year);
}
