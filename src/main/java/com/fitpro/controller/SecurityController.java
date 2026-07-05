package com.fitpro.controller;

import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.security.AuditLogResponse;
import com.fitpro.service.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
@Tag(name = "Security")
public class SecurityController {

    private final SecurityService securityService;

    @GetMapping("/audit-logs")
    public PageResponse<AuditLogResponse> auditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return securityService.auditLogs(page, size);
    }
}
