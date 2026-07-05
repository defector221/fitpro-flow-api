package com.fitpro.dto.finance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ExpenseResponse {
    private UUID id;
    private String category;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String vendor;
    private BigDecimal gstAmount;
}
