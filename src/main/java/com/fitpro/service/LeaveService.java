package com.fitpro.service;

import com.fitpro.domain.entity.Employee;
import com.fitpro.domain.entity.LeaveRequest;
import com.fitpro.domain.entity.LeaveType;
import com.fitpro.domain.enums.LeaveRequestStatus;
import com.fitpro.domain.repository.EmployeeRepository;
import com.fitpro.domain.repository.LeaveRequestRepository;
import com.fitpro.domain.repository.LeaveTypeRepository;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.leave.*;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public List<LeaveTypeResponse> listTypes() {
        UUID gymId = securityUtils.currentUser().getGymId();
        return leaveTypeRepository.findByGymId(gymId).stream()
                .map(t -> LeaveTypeResponse.builder()
                        .id(t.getId()).code(t.getCode()).name(t.getName())
                        .paid(t.isPaid()).maxDays(t.getMaxDays()).build())
                .toList();
    }

    public PageResponse<LeaveRequestResponse> list(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                leaveRequestRepository.findByGym(gymId, PageRequest.of(page, size)),
                this::toResponse);
    }

    @Transactional
    public LeaveRequestResponse create(CreateLeaveRequestDto request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (!employee.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Employee not found");
        }
        leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        LeaveRequest lr = LeaveRequest.builder()
                .employeeId(request.getEmployeeId())
                .leaveTypeId(request.getLeaveTypeId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .days(request.getDays())
                .reason(request.getReason())
                .status(LeaveRequestStatus.PENDING)
                .build();
        lr = leaveRequestRepository.save(lr);
        auditService.log("CREATE", "LeaveRequest", lr.getId(), null);
        return toResponse(lr);
    }

    @Transactional
    public LeaveRequestResponse review(UUID id, ReviewLeaveRequest request) {
        LeaveRequest lr = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found"));
        verifyEmployeeGym(lr.getEmployeeId());
        lr.setStatus(request.isApproved() ? LeaveRequestStatus.APPROVED : LeaveRequestStatus.REJECTED);
        lr.setApprovedBy(securityUtils.currentUser().getId());
        lr.setApprovedAt(Instant.now());
        return toResponse(leaveRequestRepository.save(lr));
    }

    private void verifyEmployeeGym(UUID employeeId) {
        Employee e = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (!e.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Leave request not found");
        }
    }

    private LeaveRequestResponse toResponse(LeaveRequest lr) {
        Employee e = employeeRepository.findById(lr.getEmployeeId()).orElse(null);
        LeaveType lt = leaveTypeRepository.findById(lr.getLeaveTypeId()).orElse(null);
        return LeaveRequestResponse.builder()
                .id(lr.getId())
                .employeeId(lr.getEmployeeId())
                .employeeName(e != null ? e.getFirstName() + " " + e.getLastName() : "Unknown")
                .leaveTypeName(lt != null ? lt.getName() : "—")
                .startDate(lr.getStartDate())
                .endDate(lr.getEndDate())
                .days(lr.getDays())
                .reason(lr.getReason())
                .status(lr.getStatus())
                .build();
    }
}
