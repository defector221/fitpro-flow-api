package com.fitpro.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateEmployeeRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String emergencyName;
    private String emergencyPhone;
    private String department;
    private String designation;

    @NotNull
    private LocalDate joiningDate;

    private BigDecimal basicSalary;
    private UUID branchId;
}
