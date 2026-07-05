package com.fitpro.controller;

import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.finance.*;
import com.fitpro.service.FinanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@Tag(name = "Finance")
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping("/summary")
    public FinanceSummaryResponse summary() {
        return financeService.summary();
    }

    @GetMapping("/payments")
    public PageResponse<PaymentResponse> payments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return financeService.listPayments(page, size);
    }

    @GetMapping("/expenses")
    public PageResponse<ExpenseResponse> expenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return financeService.listExpenses(page, size);
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> recordPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.recordPayment(request));
    }

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponse> recordExpense(@Valid @RequestBody CreateExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeService.recordExpense(request));
    }
}
