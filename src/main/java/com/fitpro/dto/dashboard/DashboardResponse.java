package com.fitpro.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal todayRevenue;
    private long checkInsToday;
    private long newAdmissionsToday;
    private long renewalsDueThisWeek;
    private BigDecimal pendingPayments;
    private BigDecimal payrollPending;
    private List<RevenueDataPoint> revenueChart;
    private List<CheckInSummary> recentCheckIns;
    private List<BirthdaySummary> upcomingBirthdays;
}
