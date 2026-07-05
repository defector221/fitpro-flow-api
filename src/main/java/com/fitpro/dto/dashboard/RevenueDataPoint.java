package com.fitpro.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RevenueDataPoint {
    private String date;
    private BigDecimal amount;
}
