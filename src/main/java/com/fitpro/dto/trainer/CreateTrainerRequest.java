package com.fitpro.dto.trainer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateTrainerRequest {
    @NotNull
    private UUID employeeId;
    private String specialization;
    private String bio;
}
