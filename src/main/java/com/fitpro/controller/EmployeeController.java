package com.fitpro.controller;

import com.fitpro.domain.enums.EmploymentStatus;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.employee.CreateEmployeeRequest;
import com.fitpro.dto.employee.EmployeeResponse;
import com.fitpro.dto.employee.UpdateEmployeeRequest;
import com.fitpro.service.EmployeeService;
import com.fitpro.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@Tag(name = "Staff")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final FileStorageService fileStorageService;

    @GetMapping
    @Operation(summary = "List staff with pagination and search")
    public ResponseEntity<PageResponse<EmployeeResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmploymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(employeeService.list(search, status, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get staff member by ID")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Onboard a new employee")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @PostMapping("/{id}/photo")
    @Operation(summary = "Upload staff profile photo")
    public ResponseEntity<EmployeeResponse> uploadPhoto(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file) {
        var upload = fileStorageService.storeImage(file, "staff");
        return ResponseEntity.ok(employeeService.updatePhoto(id, upload.getObjectKey()));
    }
}
