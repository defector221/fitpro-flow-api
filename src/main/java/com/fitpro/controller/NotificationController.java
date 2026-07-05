package com.fitpro.controller;

import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.notification.NotificationResponse;
import com.fitpro.dto.notification.UnreadCountResponse;
import com.fitpro.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public PageResponse<NotificationResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return notificationService.list(page, size);
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse unreadCount() {
        return UnreadCountResponse.builder()
                .unread(notificationService.unreadCount())
                .build();
    }

    @PostMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable UUID id) {
        return notificationService.markRead(id);
    }
}
