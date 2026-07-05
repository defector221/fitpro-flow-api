package com.fitpro.dto.settings;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SettingsResponse {
    private GymProfileResponse gym;
    private List<BranchResponse> branches;
    private Map<String, Object> workingHours;
    private Map<String, Object> taxConfig;
    private Map<String, Object> notificationConfig;
    private Map<String, Object> paymentConfig;
}
