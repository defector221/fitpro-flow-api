package com.fitpro.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckInSummary {
    private String memberName;
    private String planName;
    private String checkInTime;
    private String trainerName;
}
