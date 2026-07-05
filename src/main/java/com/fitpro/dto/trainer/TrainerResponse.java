package com.fitpro.dto.trainer;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TrainerResponse {
    private UUID id;
    private UUID employeeId;
    private String employeeCode;
    private String name;
    private String specialization;
    private String bio;
    private boolean active;
    private int assignedMembers;
    private List<AssignmentResponse> assignments;
}
