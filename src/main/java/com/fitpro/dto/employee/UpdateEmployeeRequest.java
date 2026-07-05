package com.fitpro.dto.employee;

import com.fitpro.domain.enums.EmploymentStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateEmployeeRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private BigDecimal basicSalary;
    private EmploymentStatus employmentStatus;
}
