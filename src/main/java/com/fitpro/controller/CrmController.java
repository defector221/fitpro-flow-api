package com.fitpro.controller;

import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.crm.*;
import com.fitpro.service.LeadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crm")
@RequiredArgsConstructor
@Tag(name = "CRM")
public class CrmController {

    private final LeadService leadService;

    @GetMapping("/summary")
    public CrmSummaryResponse summary() {
        return leadService.summary();
    }

    @GetMapping("/leads")
    public PageResponse<LeadResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return leadService.list(page, size);
    }

    @PostMapping("/leads")
    public ResponseEntity<LeadResponse> create(@Valid @RequestBody CreateLeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.create(request));
    }

    @PutMapping("/leads/{id}")
    public LeadResponse update(@PathVariable UUID id, @RequestBody UpdateLeadRequest request) {
        return leadService.update(id, request);
    }
}
