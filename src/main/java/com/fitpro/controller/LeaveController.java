package com.fitpro.controller;

import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.leave.*;
import com.fitpro.service.LeaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leave")
@RequiredArgsConstructor
@Tag(name = "Leave")
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/types")
    public List<LeaveTypeResponse> types() {
        return leaveService.listTypes();
    }

    @GetMapping
    public PageResponse<LeaveRequestResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return leaveService.list(page, size);
    }

    @PostMapping
    public ResponseEntity<LeaveRequestResponse> create(@Valid @RequestBody CreateLeaveRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.create(request));
    }

    @PostMapping("/{id}/review")
    public LeaveRequestResponse review(@PathVariable UUID id, @RequestBody ReviewLeaveRequest request) {
        return leaveService.review(id, request);
    }
}
