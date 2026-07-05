package com.fitpro.dto.checkin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MemberCheckInRequest {
    @NotNull
    private UUID memberId;
    private String source;
}
