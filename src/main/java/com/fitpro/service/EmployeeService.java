package com.fitpro.service;

import com.fitpro.domain.entity.Employee;
import com.fitpro.domain.enums.EmploymentStatus;
import com.fitpro.domain.repository.EmployeeRepository;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.employee.CreateEmployeeRequest;
import com.fitpro.dto.employee.EmployeeResponse;
import com.fitpro.dto.employee.UpdateEmployeeRequest;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;
    private final FileStorageService fileStorageService;

    public PageResponse<EmployeeResponse> list(String search, EmploymentStatus status, int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        Page<Employee> result = employeeRepository.search(
                gymId, blankToNull(search), status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageMapper.toPageResponse(result, this::toResponse);
    }

    public EmployeeResponse getById(UUID id) {
        return toResponse(findEmployee(id));
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        var user = securityUtils.currentUser();
        UUID gymId = user.getGymId();
        UUID branchId = request.getBranchId() != null ? request.getBranchId() : user.getBranchId();

        String code = generateEmployeeCode(gymId);

        Employee employee = Employee.builder()
                .gymId(gymId)
                .branchId(branchId)
                .employeeCode(code)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .emergencyName(request.getEmergencyName())
                .emergencyPhone(request.getEmergencyPhone())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .photoUrl(request.getPhotoUrl())
                .joiningDate(request.getJoiningDate())
                .basicSalary(request.getBasicSalary())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .qrCode("QR-" + code)
                .build();

        employee = employeeRepository.save(employee);
        auditService.log("CREATE", "Employee", employee.getId(), Map.of("employeeCode", code));
        return toResponse(employee);
    }

    @Transactional
    public EmployeeResponse update(UUID id, UpdateEmployeeRequest request) {
        Employee employee = findEmployee(id);
        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getEmail() != null) employee.setEmail(request.getEmail());
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) employee.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) employee.setGender(request.getGender());
        if (request.getAddress() != null) employee.setAddress(request.getAddress());
        if (request.getEmergencyName() != null) employee.setEmergencyName(request.getEmergencyName());
        if (request.getEmergencyPhone() != null) employee.setEmergencyPhone(request.getEmergencyPhone());
        if (request.getDepartment() != null) employee.setDepartment(request.getDepartment());
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation());
        if (request.getPhotoUrl() != null) employee.setPhotoUrl(request.getPhotoUrl());
        if (request.getBasicSalary() != null) employee.setBasicSalary(request.getBasicSalary());
        if (request.getEmploymentStatus() != null) employee.setEmploymentStatus(request.getEmploymentStatus());

        employee = employeeRepository.save(employee);
        auditService.log("UPDATE", "Employee", employee.getId(), Map.of("status", employee.getEmploymentStatus().name()));
        return toResponse(employee);
    }

    @Transactional
    public EmployeeResponse updatePhoto(UUID id, String photoUrl) {
        Employee employee = findEmployee(id);
        employee.setPhotoUrl(photoUrl);
        employee = employeeRepository.save(employee);
        auditService.log("UPDATE_PHOTO", "Employee", employee.getId(), Map.of("employeeCode", employee.getEmployeeCode()));
        return toResponse(employee);
    }

    private Employee findEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (!employee.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Employee not found");
        }
        return employee;
    }

    private String generateEmployeeCode(UUID gymId) {
        long count = employeeRepository.findByGymId(gymId, PageRequest.of(0, 1)).getTotalElements();
        return String.format("EMP%05d", count + 1);
    }

    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .employeeCode(e.getEmployeeCode())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .dateOfBirth(e.getDateOfBirth())
                .gender(e.getGender())
                .department(e.getDepartment())
                .designation(e.getDesignation())
                .address(e.getAddress())
                .emergencyName(e.getEmergencyName())
                .emergencyPhone(e.getEmergencyPhone())
                .photoUrl(fileStorageService.signedUrl(e.getPhotoUrl()))
                .joiningDate(e.getJoiningDate())
                .employmentStatus(e.getEmploymentStatus())
                .basicSalary(e.getBasicSalary())
                .branchId(e.getBranchId())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
