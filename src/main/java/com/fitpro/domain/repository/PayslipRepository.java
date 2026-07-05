package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PayslipRepository extends JpaRepository<Payslip, UUID> {
    List<Payslip> findByPayrollRunId(UUID payrollRunId);
}
