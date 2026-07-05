package com.fitpro.dto.finance;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {
    private UUID id;
    private UUID memberId;
    private String memberName;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentType;
    private String status;
    private String referenceNo;
    private Instant paidAt;
}
