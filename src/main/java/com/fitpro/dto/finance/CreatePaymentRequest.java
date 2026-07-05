package com.fitpro.dto.finance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreatePaymentRequest {
    private UUID memberId;
    @NotNull
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentType;
    private String referenceNo;
    private String notes;
}
