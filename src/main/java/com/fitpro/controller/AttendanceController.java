package com.fitpro.controller;

import com.fitpro.dto.attendance.AttendanceResponse;
import com.fitpro.dto.attendance.MarkAttendanceRequest;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.service.AttendanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public PageResponse<AttendanceResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return attendanceService.list(page, size);
    }

    @GetMapping("/date/{date}")
    public List<AttendanceResponse> byDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.listByDate(date);
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> mark(@Valid @RequestBody MarkAttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.mark(request));
    }

    @PostMapping("/{id}/approve")
    public AttendanceResponse approve(@PathVariable UUID id) {
        return attendanceService.approve(id);
    }
}
