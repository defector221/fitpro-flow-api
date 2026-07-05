package com.fitpro.dto.leave;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateLeaveRequestDto {
    @NotNull
    private UUID employeeId;
    @NotNull
    private UUID leaveTypeId;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private BigDecimal days;
    private String reason;
}
