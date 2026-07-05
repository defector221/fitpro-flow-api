package com.fitpro.dto.leave;

import com.fitpro.domain.enums.LeaveRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class LeaveRequestResponse {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal days;
    private String reason;
    private LeaveRequestStatus status;
}
