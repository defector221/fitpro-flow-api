package com.fitpro.dto.payroll;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PayslipResponse {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private BigDecimal basic;
    private BigDecimal hra;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private String status;
}
