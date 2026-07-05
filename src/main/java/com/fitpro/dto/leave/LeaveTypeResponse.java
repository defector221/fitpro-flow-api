package com.fitpro.dto.leave;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LeaveTypeResponse {
    private UUID id;
    private String code;
    private String name;
    private boolean paid;
    private Integer maxDays;
}
