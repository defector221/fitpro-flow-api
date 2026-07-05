package com.fitpro.dto.trainer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AssignmentResponse {
    private UUID id;
    private UUID memberId;
    private String memberName;
    private LocalDate startDate;
    private boolean active;
}
