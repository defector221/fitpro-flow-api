package com.fitpro.dto.finance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateExpenseRequest {
    @NotNull
    private String category;
    private String description;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDate expenseDate;
    private String vendor;
    private BigDecimal gstAmount;
}
