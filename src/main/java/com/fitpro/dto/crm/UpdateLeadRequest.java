package com.fitpro.dto.crm;

import com.fitpro.domain.enums.LeadStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateLeadRequest {
    private String name;
    private String email;
    private String phone;
    private LeadStatus status;
    private String notes;
    private LocalDate followUpDate;
}
