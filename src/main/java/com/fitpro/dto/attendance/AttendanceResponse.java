package com.fitpro.dto.attendance;

import com.fitpro.domain.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AttendanceResponse {
    private UUID id;
    private UUID employeeId;
    private String employeeCode;
    private String employeeName;
    private LocalDate attendanceDate;
    private Instant checkIn;
    private Instant checkOut;
    private AttendanceStatus status;
    private String source;
    private boolean approved;
}
