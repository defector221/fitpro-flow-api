package com.fitpro.dto.crm;

import com.fitpro.domain.enums.LeadStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class LeadResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String source;
    private LeadStatus status;
    private String notes;
    private LocalDate followUpDate;
    private UUID convertedMemberId;
}
