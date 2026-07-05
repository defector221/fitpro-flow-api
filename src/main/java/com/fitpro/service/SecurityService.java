package com.fitpro.service;

import com.fitpro.domain.repository.AuditLogRepository;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.security.AuditLogResponse;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityUtils securityUtils;

    public PageResponse<AuditLogResponse> auditLogs(int page, int size) {
        var gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                auditLogRepository.findByGymIdOrderByCreatedAtDesc(gymId, PageRequest.of(page, size)),
                log -> AuditLogResponse.builder()
                        .id(log.getId())
                        .userId(log.getUserId())
                        .action(log.getAction())
                        .entityType(log.getEntityType())
                        .entityId(log.getEntityId())
                        .newValue(log.getNewValue())
                        .ipAddress(log.getIpAddress())
                        .createdAt(log.getCreatedAt())
                        .build());
    }
}
