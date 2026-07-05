package com.fitpro.dto.plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePlanRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;

    @NotNull @Positive
    private Integer durationDays;

    @NotNull @Positive
    private BigDecimal price;

    private String planType;
}
