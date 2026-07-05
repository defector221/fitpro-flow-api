package com.fitpro.dto.employee;

import com.fitpro.domain.enums.EmploymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateEmployeeRequest {
    private String firstName;
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
    private String photoUrl;
    private BigDecimal basicSalary;
    private EmploymentStatus employmentStatus;
}
