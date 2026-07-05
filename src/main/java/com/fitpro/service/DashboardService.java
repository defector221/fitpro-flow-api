package com.fitpro.service;

import com.fitpro.domain.entity.Member;
import com.fitpro.domain.entity.MemberCheckIn;
import com.fitpro.domain.entity.Membership;
import com.fitpro.domain.entity.MembershipPlan;
import com.fitpro.domain.enums.MembershipStatus;
import com.fitpro.domain.repository.*;
import com.fitpro.dto.dashboard.*;
import com.fitpro.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PaymentRepository paymentRepository;
    private final MemberCheckInRepository checkInRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository planRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtils securityUtils;

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    public DashboardResponse getDashboard() {
        UUID gymId = securityUtils.currentUser().getGymId();
        LocalDate today = LocalDate.now(ZONE);
        Instant dayStart = today.atStartOfDay(ZONE).toInstant();
        Instant dayEnd = today.plusDays(1).atStartOfDay(ZONE).toInstant();

        BigDecimal todayRevenue = paymentRepository.sumRevenueByDateRange(gymId, dayStart, dayEnd);
        long checkIns = checkInRepository.countByGymAndDateRange(gymId, dayStart, dayEnd);
        long newAdmissions = memberRepository.countByGymIdAndCreatedAtAfter(gymId, dayStart);
        long renewalsDue = membershipRepository.countRenewalsDue(
                gymId, today, today.plusDays(7), MembershipStatus.ACTIVE);
        BigDecimal pendingPayments = paymentRepository.sumPendingPayments(gymId);

        // Payroll pending: sum of active employee basic salaries (simplified)
        BigDecimal payrollPending = employeeRepository.findByGymId(gymId, PageRequest.of(0, 1000))
                .stream()
                .filter(e -> e.getBasicSalary() != null)
                .map(e -> e.getBasicSalary())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardResponse.builder()
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .checkInsToday(checkIns)
                .newAdmissionsToday(newAdmissions)
                .renewalsDueThisWeek(renewalsDue)
                .pendingPayments(pendingPayments != null ? pendingPayments : BigDecimal.ZERO)
                .payrollPending(payrollPending)
                .revenueChart(buildRevenueChart(gymId))
                .recentCheckIns(buildRecentCheckIns(gymId))
                .upcomingBirthdays(buildBirthdays(gymId))
                .build();
    }

    private List<RevenueDataPoint> buildRevenueChart(UUID gymId) {
        List<RevenueDataPoint> points = new ArrayList<>();
        LocalDate today = LocalDate.now(ZONE);
        for (int i = 13; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Instant start = date.atStartOfDay(ZONE).toInstant();
            Instant end = date.plusDays(1).atStartOfDay(ZONE).toInstant();
            BigDecimal amount = paymentRepository.sumRevenueByDateRange(gymId, start, end);
            points.add(RevenueDataPoint.builder()
                    .date(date.toString())
                    .amount(amount != null ? amount : BigDecimal.ZERO)
                    .build());
        }
        return points;
    }

    private List<CheckInSummary> buildRecentCheckIns(UUID gymId) {
        return checkInRepository.findRecentByGym(gymId, PageRequest.of(0, 5))
                .stream()
                .map(this::toCheckInSummary)
                .toList();
    }

    private CheckInSummary toCheckInSummary(MemberCheckIn checkIn) {
        Member member = memberRepository.findById(checkIn.getMemberId()).orElse(null);
        String memberName = member != null ? member.getFirstName() + " " + member.getLastName() : "Unknown";
        String planName = "—";
        if (member != null) {
            List<Membership> memberships = membershipRepository.findByMemberIdOrderByStartDateDesc(member.getId());
            if (!memberships.isEmpty()) {
                planName = planRepository.findById(memberships.get(0).getPlanId())
                        .map(MembershipPlan::getName).orElse("—");
            }
        }
        return CheckInSummary.builder()
                .memberName(memberName)
                .planName(planName)
                .checkInTime(TIME_FMT.format(checkIn.getCheckInAt().atZone(ZONE)))
                .trainerName("—")
                .build();
    }

    private List<BirthdaySummary> buildBirthdays(UUID gymId) {
        LocalDate today = LocalDate.now(ZONE);
        return memberRepository.findByGymId(gymId, PageRequest.of(0, 200))
                .stream()
                .filter(m -> m.getDateOfBirth() != null)
                .filter(m -> {
                    LocalDate dob = m.getDateOfBirth().withYear(today.getYear());
                    long days = ChronoUnit.DAYS.between(today, dob);
                    return days >= 0 && days <= 7;
                })
                .sorted((a, b) -> {
                    int dayA = a.getDateOfBirth().getDayOfYear();
                    int dayB = b.getDateOfBirth().getDayOfYear();
                    return Integer.compare(dayA, dayB);
                })
                .limit(5)
                .map(m -> BirthdaySummary.builder()
                        .name(m.getFirstName() + " " + m.getLastName().charAt(0) + ".")
                        .date(formatBirthday(m.getDateOfBirth(), today))
                        .build())
                .toList();
    }

    private String formatBirthday(LocalDate dob, LocalDate today) {
        LocalDate next = dob.withYear(today.getYear());
        long days = ChronoUnit.DAYS.between(today, next);
        if (days == 0) return "Today";
        if (days == 1) return "Tomorrow";
        return next.getDayOfWeek().name().substring(0, 3);
    }
}
