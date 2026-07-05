package com.fitpro.dto.member;

import com.fitpro.domain.enums.MemberStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class MemberResponse {
    private UUID id;
    private String memberCode;
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
    private BigDecimal bmi;
    private String fitnessGoals;
    private String medicalNotes;
    private String photoUrl;
    private MemberStatus status;
    private UUID branchId;
    private String activePlanName;
    private LocalDate membershipEndDate;
    private Instant createdAt;
}
