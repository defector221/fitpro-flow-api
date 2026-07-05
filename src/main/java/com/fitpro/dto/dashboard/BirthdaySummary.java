package com.fitpro.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BirthdaySummary {
    private String name;
    private String date;
}
