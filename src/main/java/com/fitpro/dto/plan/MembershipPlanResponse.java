package com.fitpro.dto.plan;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MembershipPlanResponse {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private Integer durationDays;
    private BigDecimal price;
    private String planType;
    private boolean active;
    private Instant createdAt;
}
