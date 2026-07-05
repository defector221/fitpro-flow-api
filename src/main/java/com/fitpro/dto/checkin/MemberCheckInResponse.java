package com.fitpro.dto.checkin;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class MemberCheckInResponse {
    private UUID id;
    private UUID memberId;
    private String memberCode;
    private String memberName;
    private String planName;
    private Instant checkInAt;
    private Instant checkOutAt;
    private String source;
}
