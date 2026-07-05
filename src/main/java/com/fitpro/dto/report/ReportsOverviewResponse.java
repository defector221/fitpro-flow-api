package com.fitpro.dto.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ReportsOverviewResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private long activeMembers;
    private long activeStaff;
    private long checkInsThisMonth;
    private long newMembersThisMonth;
    private long pendingLeaves;
    private double leadConversionRate;
    private List<MonthlyStat> revenueByMonth;
    private List<MonthlyStat> expensesByMonth;
}
