package com.fitpro.controller;

import com.fitpro.dto.plan.CreatePlanRequest;
import com.fitpro.dto.plan.MembershipPlanResponse;
import com.fitpro.service.MembershipPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Tag(name = "Membership Plans")
public class MembershipPlanController {

    private final MembershipPlanService planService;

    @GetMapping
    @Operation(summary = "List all membership plans")
    public ResponseEntity<List<MembershipPlanResponse>> listAll() {
        return ResponseEntity.ok(planService.listAll());
    }

    @GetMapping("/active")
    @Operation(summary = "List active membership plans")
    public ResponseEntity<List<MembershipPlanResponse>> listActive() {
        return ResponseEntity.ok(planService.listActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plan by ID")
    public ResponseEntity<MembershipPlanResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(planService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a membership plan")
    public ResponseEntity<MembershipPlanResponse> create(@Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.create(request));
    }
}
