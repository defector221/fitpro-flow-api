package com.fitpro.service;

import com.fitpro.domain.entity.Expense;
import com.fitpro.domain.entity.Member;
import com.fitpro.domain.entity.Payment;
import com.fitpro.domain.repository.ExpenseRepository;
import com.fitpro.domain.repository.MemberRepository;
import com.fitpro.domain.repository.PaymentRepository;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.finance.*;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    public FinanceSummaryResponse summary() {
        UUID gymId = securityUtils.currentUser().getGymId();
        LocalDate now = LocalDate.now(ZONE);
        LocalDate monthStart = now.withDayOfMonth(1);
        Instant start = monthStart.atStartOfDay(ZONE).toInstant();
        Instant end = now.plusDays(1).atStartOfDay(ZONE).toInstant();

        BigDecimal income = paymentRepository.sumRevenueByDateRange(gymId, start, end);
        BigDecimal expenses = expenseRepository.sumByDateRange(gymId, monthStart, now);
        BigDecimal pending = paymentRepository.sumPendingPayments(gymId);

        income = income != null ? income : BigDecimal.ZERO;
        expenses = expenses != null ? expenses : BigDecimal.ZERO;
        pending = pending != null ? pending : BigDecimal.ZERO;

        return FinanceSummaryResponse.builder()
                .totalIncome(income)
                .totalExpenses(expenses)
                .netProfit(income.subtract(expenses))
                .pendingPayments(pending)
                .build();
    }

    public PageResponse<PaymentResponse> listPayments(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                paymentRepository.findByGymIdOrderByPaidAtDesc(gymId, PageRequest.of(page, size)),
                this::toPaymentResponse);
    }

    public PageResponse<ExpenseResponse> listExpenses(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                expenseRepository.findByGymIdOrderByExpenseDateDesc(gymId, PageRequest.of(page, size)),
                this::toExpenseResponse);
    }

    @Transactional
    public PaymentResponse recordPayment(CreatePaymentRequest request) {
        var user = securityUtils.currentUser();
        Payment payment = Payment.builder()
                .gymId(user.getGymId())
                .branchId(user.getBranchId())
                .memberId(request.getMemberId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH")
                .paymentType(request.getPaymentType() != null ? request.getPaymentType() : "MEMBERSHIP")
                .status("COMPLETED")
                .referenceNo(request.getReferenceNo())
                .notes(request.getNotes())
                .paidAt(Instant.now())
                .build();
        payment = paymentRepository.save(payment);
        auditService.log("CREATE", "Payment", payment.getId(), null);
        return toPaymentResponse(payment);
    }

    @Transactional
    public ExpenseResponse recordExpense(CreateExpenseRequest request) {
        var user = securityUtils.currentUser();
        Expense expense = Expense.builder()
                .gymId(user.getGymId())
                .branchId(user.getBranchId())
                .category(request.getCategory())
                .description(request.getDescription())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .vendor(request.getVendor())
                .gstAmount(request.getGstAmount() != null ? request.getGstAmount() : BigDecimal.ZERO)
                .createdBy(user.getId())
                .build();
        expense = expenseRepository.save(expense);
        auditService.log("CREATE", "Expense", expense.getId(), null);
        return toExpenseResponse(expense);
    }

    private PaymentResponse toPaymentResponse(Payment p) {
        Member m = p.getMemberId() != null ? memberRepository.findById(p.getMemberId()).orElse(null) : null;
        return PaymentResponse.builder()
                .id(p.getId())
                .memberId(p.getMemberId())
                .memberName(m != null ? m.getFirstName() + " " + m.getLastName() : null)
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .paymentType(p.getPaymentType())
                .status(p.getStatus())
                .referenceNo(p.getReferenceNo())
                .paidAt(p.getPaidAt())
                .build();
    }

    private ExpenseResponse toExpenseResponse(Expense e) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .category(e.getCategory())
                .description(e.getDescription())
                .amount(e.getAmount())
                .expenseDate(e.getExpenseDate())
                .vendor(e.getVendor())
                .gstAmount(e.getGstAmount())
                .build();
    }
}
