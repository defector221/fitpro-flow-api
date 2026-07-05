package com.fitpro.dto.payroll;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PayrollRunSummaryResponse {
    private UUID id;
    private int periodMonth;
    private int periodYear;
    private String status;
    private BigDecimal totalAmount;
    private Instant processedAt;
    private int payslipCount;
}
