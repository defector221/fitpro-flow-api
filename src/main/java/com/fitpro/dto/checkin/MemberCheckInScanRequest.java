package com.fitpro.dto.checkin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberCheckInScanRequest {
    @NotBlank
    private String memberCode;

    private String action = "CHECK_IN";
    private String source = "ID_CARD";
}
