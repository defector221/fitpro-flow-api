package com.fitpro.dto.member;

import com.fitpro.domain.enums.MemberStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateMemberRequest {
    private String firstName;
    private String lastName;
    private String email;
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
    private MemberStatus status;
}
