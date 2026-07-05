package com.fitpro.service;

import com.fitpro.domain.enums.EmploymentStatus;
import com.fitpro.domain.enums.LeadStatus;
import com.fitpro.domain.enums.LeaveRequestStatus;
import com.fitpro.domain.enums.MemberStatus;
import com.fitpro.domain.repository.*;
import com.fitpro.dto.report.MonthlyStat;
import com.fitpro.dto.report.ReportsOverviewResponse;
import com.fitpro.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final MemberCheckInRepository checkInRepository;
    private final LeadRepository leadRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final SecurityUtils securityUtils;

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    public ReportsOverviewResponse overview() {
        UUID gymId = securityUtils.currentUser().getGymId();
        LocalDate now = LocalDate.now(ZONE);
        LocalDate monthStart = now.withDayOfMonth(1);
        Instant monthStartInstant = monthStart.atStartOfDay(ZONE).toInstant();
        Instant nowInstant = now.plusDays(1).atStartOfDay(ZONE).toInstant();

        BigDecimal revenue = paymentRepository.sumRevenueByDateRange(gymId, monthStartInstant, nowInstant);
        BigDecimal expenses = expenseRepository.sumByDateRange(gymId, monthStart, now);
        revenue = revenue != null ? revenue : BigDecimal.ZERO;
        expenses = expenses != null ? expenses : BigDecimal.ZERO;

        long activeMembers = memberRepository.search(gymId, null, MemberStatus.ACTIVE, PageRequest.of(0, 1)).getTotalElements();
        long activeStaff = employeeRepository.search(gymId, null, EmploymentStatus.ACTIVE, PageRequest.of(0, 1)).getTotalElements();
        long checkIns = checkInRepository.countByGymAndDateRange(gymId, monthStartInstant, nowInstant);
        long newMembers = memberRepository.countByGymIdAndCreatedAtAfter(gymId, monthStartInstant);

        long totalLeads = leadRepository.findByGymId(gymId, PageRequest.of(0, 1)).getTotalElements();
        long wonLeads = leadRepository.countByGymIdAndStatus(gymId, LeadStatus.WON);
        double conversionRate = totalLeads > 0 ? (wonLeads * 100.0 / totalLeads) : 0;

        long pendingLeaves = leaveRequestRepository.countByGymAndStatus(gymId, LeaveRequestStatus.PENDING);

        return ReportsOverviewResponse.builder()
                .totalRevenue(revenue)
                .totalExpenses(expenses)
                .netProfit(revenue.subtract(expenses))
                .activeMembers(activeMembers)
                .activeStaff(activeStaff)
                .checkInsThisMonth(checkIns)
                .newMembersThisMonth(newMembers)
                .pendingLeaves(pendingLeaves)
                .leadConversionRate(Math.round(conversionRate * 10) / 10.0)
                .revenueByMonth(buildMonthlyRevenue(gymId))
                .expensesByMonth(buildMonthlyExpenses(gymId))
                .build();
    }

    private List<MonthlyStat> buildMonthlyRevenue(UUID gymId) {
        List<MonthlyStat> stats = new ArrayList<>();
        LocalDate now = LocalDate.now(ZONE);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            LocalDate start = month.withDayOfMonth(1);
            LocalDate end = start.plusMonths(1);
            BigDecimal amount = paymentRepository.sumRevenueByDateRange(
                    gymId, start.atStartOfDay(ZONE).toInstant(), end.atStartOfDay(ZONE).toInstant());
            stats.add(MonthlyStat.builder()
                    .month(start.format(fmt))
                    .amount(amount != null ? amount : BigDecimal.ZERO)
                    .build());
        }
        return stats;
    }

    private List<MonthlyStat> buildMonthlyExpenses(UUID gymId) {
        List<MonthlyStat> stats = new ArrayList<>();
        LocalDate now = LocalDate.now(ZONE);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            LocalDate start = month.withDayOfMonth(1);
            LocalDate end = start.plusMonths(1).minusDays(1);
            BigDecimal amount = expenseRepository.sumByDateRange(gymId, start, end);
            stats.add(MonthlyStat.builder()
                    .month(start.format(fmt))
                    .amount(amount != null ? amount : BigDecimal.ZERO)
                    .build());
        }
        return stats;
    }
}
