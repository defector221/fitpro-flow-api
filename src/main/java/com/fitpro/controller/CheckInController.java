package com.fitpro.controller;

import com.fitpro.dto.checkin.MemberCheckInRequest;
import com.fitpro.dto.checkin.MemberCheckInResponse;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.service.CheckInService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkin")
@RequiredArgsConstructor
@Tag(name = "Check-in")
public class CheckInController {

    private final CheckInService checkInService;

    @GetMapping
    public PageResponse<MemberCheckInResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return checkInService.listAll(page, size);
    }

    @GetMapping("/today")
    public PageResponse<MemberCheckInResponse> today(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return checkInService.listToday(page, size);
    }

    @PostMapping
    public ResponseEntity<MemberCheckInResponse> checkIn(@Valid @RequestBody MemberCheckInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(checkInService.checkIn(request));
    }

    @PostMapping("/{id}/checkout")
    public MemberCheckInResponse checkOut(@PathVariable UUID id) {
        return checkInService.checkOut(id);
    }
}
