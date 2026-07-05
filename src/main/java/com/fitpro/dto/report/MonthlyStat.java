package com.fitpro.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyStat {
    private String month;
    private BigDecimal amount;
}
