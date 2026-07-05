package com.fitpro.dto.crm;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateLeadRequest {
    @NotBlank
    private String name;
    private String email;
    private String phone;
    private String source;
    private String notes;
    private LocalDate followUpDate;
    private UUID assignedTo;
}
