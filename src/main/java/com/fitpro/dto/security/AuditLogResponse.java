package com.fitpro.dto.security;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private UUID userId;
    private String action;
    private String entityType;
    private UUID entityId;
    private Map<String, Object> newValue;
    private String ipAddress;
    private Instant createdAt;
}
