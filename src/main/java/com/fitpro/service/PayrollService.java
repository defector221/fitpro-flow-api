package com.fitpro.service;

import com.fitpro.domain.entity.*;
import com.fitpro.domain.enums.EmploymentStatus;
import com.fitpro.domain.enums.PayrollRunStatus;
import com.fitpro.domain.repository.*;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.payroll.*;
import com.fitpro.exception.BusinessException;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollRunRepository payrollRunRepository;
    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public PageResponse<PayrollRunSummaryResponse> list(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                payrollRunRepository.findByGymIdOrderByCreatedAtDesc(gymId, PageRequest.of(page, size)),
                r -> PayrollRunSummaryResponse.builder()
                        .id(r.getId())
                        .periodMonth(r.getPeriodMonth())
                        .periodYear(r.getPeriodYear())
                        .status(r.getStatus().name())
                        .totalAmount(r.getTotalAmount())
                        .processedAt(r.getProcessedAt())
                        .payslipCount(payslipRepository.findByPayrollRunId(r.getId()).size())
                        .build());
    }

    public PayrollRunResponse getById(UUID id) {
        PayrollRun run = findRun(id);
        List<PayslipResponse> payslips = payslipRepository.findByPayrollRunId(run.getId()).stream()
                .map(this::toPayslipResponse).toList();
        return PayrollRunResponse.builder()
                .id(run.getId())
                .periodMonth(run.getPeriodMonth())
                .periodYear(run.getPeriodYear())
                .status(run.getStatus().name())
                .totalAmount(run.getTotalAmount())
                .processedAt(run.getProcessedAt())
                .payslips(payslips)
                .build();
    }

    @Transactional
    public PayrollRunResponse generate(int month, int year) {
        UUID gymId = securityUtils.currentUser().getGymId();
        if (payrollRunRepository.findByGymIdAndPeriodMonthAndPeriodYear(gymId, month, year).isPresent()) {
            throw new BusinessException("Payroll already exists for this period");
        }

        PayrollRun run = PayrollRun.builder()
                .gymId(gymId)
                .branchId(securityUtils.currentUser().getBranchId())
                .periodMonth(month)
                .periodYear(year)
                .status(PayrollRunStatus.DRAFT)
                .build();

        List<Employee> employees = employeeRepository.findByGymId(gymId, PageRequest.of(0, 500))
                .getContent().stream()
                .filter(e -> e.getEmploymentStatus() == EmploymentStatus.ACTIVE)
                .toList();

        BigDecimal total = BigDecimal.ZERO;
        run = payrollRunRepository.save(run);

        List<Payslip> payslips = new ArrayList<>();
        for (Employee e : employees) {
            BigDecimal basic = e.getBasicSalary() != null ? e.getBasicSalary() : BigDecimal.ZERO;
            BigDecimal hra = basic.multiply(BigDecimal.valueOf(0.4)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal allowances = basic.multiply(BigDecimal.valueOf(0.1)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pf = basic.multiply(BigDecimal.valueOf(0.12)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pt = BigDecimal.valueOf(200);
            BigDecimal deductions = pf.add(pt);
            BigDecimal net = basic.add(hra).add(allowances).subtract(deductions);

            Payslip ps = Payslip.builder()
                    .payrollRunId(run.getId())
                    .employeeId(e.getId())
                    .basic(basic).hra(hra).allowances(allowances)
                    .deductions(deductions).netSalary(net)
                    .build();
            payslips.add(payslipRepository.save(ps));
            total = total.add(net);
        }

        run.setTotalAmount(total);
        run.setStatus(PayrollRunStatus.COMPLETED);
        run.setProcessedAt(Instant.now());
        payrollRunRepository.save(run);
        auditService.log("GENERATE", "PayrollRun", run.getId(), null);

        return getById(run.getId());
    }

    private PayrollRun findRun(UUID id) {
        PayrollRun run = payrollRunRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found"));
        if (!run.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Payroll run not found");
        }
        return run;
    }

    private PayslipResponse toPayslipResponse(Payslip ps) {
        Employee e = employeeRepository.findById(ps.getEmployeeId()).orElse(null);
        return PayslipResponse.builder()
                .id(ps.getId())
                .employeeId(ps.getEmployeeId())
                .employeeName(e != null ? e.getFirstName() + " " + e.getLastName() : "Unknown")
                .basic(ps.getBasic()).hra(ps.getHra()).allowances(ps.getAllowances())
                .deductions(ps.getDeductions()).netSalary(ps.getNetSalary())
                .status(ps.getStatus())
                .build();
    }
}
