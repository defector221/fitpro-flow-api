package com.fitpro.dto.trainer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AssignMemberRequest {
    @NotNull
    private UUID memberId;
    @NotNull
    private LocalDate startDate;
}
