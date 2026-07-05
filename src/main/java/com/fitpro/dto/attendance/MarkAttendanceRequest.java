package com.fitpro.dto.attendance;

import com.fitpro.domain.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class MarkAttendanceRequest {
    @NotNull
    private UUID employeeId;
    @NotNull
    private LocalDate date;
    private AttendanceStatus status;
    private String source;
    private String notes;
}
