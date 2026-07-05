package com.fitpro.dto.notification;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private String type;
    private String title;
    private String message;
    private String entityType;
    private UUID entityId;
    private boolean read;
    private Instant readAt;
    private Instant createdAt;
}
