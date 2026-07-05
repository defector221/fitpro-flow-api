package com.fitpro.service;

import com.fitpro.domain.entity.AuditLog;
import com.fitpro.domain.repository.AuditLogRepository;
import com.fitpro.security.SecurityUtils;
import com.fitpro.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final SecurityUtils securityUtils;

    public void log(String action, String entityType, UUID entityId, Map<String, Object> newValue) {
        UserPrincipal user = securityUtils.currentUserOrNull();
        AuditLog log = AuditLog.builder()
                .gymId(user != null ? user.getGymId() : null)
                .userId(user != null ? user.getId() : null)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .newValue(newValue)
                .build();
        auditLogRepository.save(log);
    }
}
