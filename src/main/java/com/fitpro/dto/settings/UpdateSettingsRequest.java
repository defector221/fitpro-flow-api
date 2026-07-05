package com.fitpro.dto.settings;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateSettingsRequest {
    private Map<String, Object> workingHours;
    private Map<String, Object> taxConfig;
    private Map<String, Object> notificationConfig;
    private Map<String, Object> paymentConfig;
}
