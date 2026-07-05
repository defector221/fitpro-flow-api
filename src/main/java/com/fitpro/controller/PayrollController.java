package com.fitpro.controller;

import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.payroll.PayrollRunResponse;
import com.fitpro.dto.payroll.PayrollRunSummaryResponse;
import com.fitpro.service.PayrollService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll")
public class PayrollController {

    private final PayrollService payrollService;

    @GetMapping
    public PageResponse<PayrollRunSummaryResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return payrollService.list(page, size);
    }

    @GetMapping("/{id}")
    public PayrollRunResponse getById(@PathVariable UUID id) {
        return payrollService.getById(id);
    }

    @PostMapping("/generate")
    public ResponseEntity<PayrollRunResponse> generate(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payrollService.generate(month, year));
    }
}
