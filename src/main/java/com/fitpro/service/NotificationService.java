package com.fitpro.service;

import com.fitpro.domain.entity.Member;
import com.fitpro.domain.entity.Notification;
import com.fitpro.domain.repository.NotificationRepository;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.notification.NotificationResponse;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SecurityUtils securityUtils;

    public PageResponse<NotificationResponse> list(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        var pageResult = notificationRepository.findByGymIdOrderByCreatedAtDesc(
                gymId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageMapper.toPageResponse(pageResult, this::toResponse);
    }

    public long unreadCount() {
        return notificationRepository.countByGymIdAndReadAtIsNull(securityUtils.currentUser().getGymId());
    }

    @Transactional
    public NotificationResponse markRead(UUID id) {
        Notification notification = findNotification(id);
        notification.setReadAt(Instant.now());
        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void memberCheckIn(Member member, UUID branchId, UUID checkInId) {
        create(
                member.getGymId(),
                branchId,
                "CHECK_IN",
                member.getFirstName() + " checked in",
                member.getFirstName() + " " + member.getLastName() + " checked in with " + member.getMemberCode(),
                "MemberCheckIn",
                checkInId);
    }

    @Transactional
    public void memberCheckOut(Member member, UUID branchId, UUID checkInId) {
        create(
                member.getGymId(),
                branchId,
                "CHECK_OUT",
                member.getFirstName() + " checked out",
                member.getFirstName() + " " + member.getLastName() + " checked out with " + member.getMemberCode(),
                "MemberCheckIn",
                checkInId);
    }

    private void create(UUID gymId, UUID branchId, String type, String title, String message, String entityType, UUID entityId) {
        notificationRepository.save(Notification.builder()
                .gymId(gymId)
                .branchId(branchId)
                .type(type)
                .title(title)
                .message(message)
                .entityType(entityType)
                .entityId(entityId)
                .build());
    }

    private Notification findNotification(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Notification not found");
        }
        return notification;
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .entityType(notification.getEntityType())
                .entityId(notification.getEntityId())
                .read(notification.getReadAt() != null)
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
