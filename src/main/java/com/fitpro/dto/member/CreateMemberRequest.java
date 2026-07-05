package com.fitpro.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateMemberRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String email;

    @NotBlank
    private String phone;

    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String emergencyName;
    private String emergencyPhone;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String fitnessGoals;
    private String medicalNotes;
    private UUID branchId;
    private UUID planId;
}
