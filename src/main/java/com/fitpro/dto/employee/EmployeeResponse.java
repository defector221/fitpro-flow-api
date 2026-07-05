package com.fitpro.dto.employee;

import com.fitpro.domain.enums.EmploymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EmployeeResponse {
    private UUID id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String department;
    private String designation;
    private String address;
    private String emergencyName;
    private String emergencyPhone;
    private String photoUrl;
    private LocalDate joiningDate;
    private EmploymentStatus employmentStatus;
    private BigDecimal basicSalary;
    private UUID branchId;
    private Instant createdAt;
}
