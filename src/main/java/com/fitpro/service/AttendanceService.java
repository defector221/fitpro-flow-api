package com.fitpro.service;

import com.fitpro.domain.entity.Employee;
import com.fitpro.domain.entity.EmployeeAttendance;
import com.fitpro.domain.enums.AttendanceStatus;
import com.fitpro.domain.repository.EmployeeAttendanceRepository;
import com.fitpro.domain.repository.EmployeeRepository;
import com.fitpro.dto.attendance.AttendanceResponse;
import com.fitpro.dto.attendance.MarkAttendanceRequest;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final EmployeeAttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public PageResponse<AttendanceResponse> list(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                attendanceRepository.findByGym(gymId, PageRequest.of(page, size)),
                this::toResponse);
    }

    public List<AttendanceResponse> listByDate(LocalDate date) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return attendanceRepository.findByGymAndDate(gymId, date).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AttendanceResponse mark(MarkAttendanceRequest request) {
        Employee employee = findEmployee(request.getEmployeeId());
        EmployeeAttendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(employee.getId(), request.getDate())
                .orElse(EmployeeAttendance.builder()
                        .employeeId(employee.getId())
                        .branchId(employee.getBranchId())
                        .attendanceDate(request.getDate())
                        .build());

        attendance.setStatus(request.getStatus() != null ? request.getStatus() : AttendanceStatus.PRESENT);
        attendance.setSource(request.getSource() != null ? request.getSource() : "MANUAL");
        attendance.setNotes(request.getNotes());
        if (attendance.getCheckIn() == null && attendance.getStatus() == AttendanceStatus.PRESENT) {
            attendance.setCheckIn(Instant.now());
        }

        attendance = attendanceRepository.save(attendance);
        auditService.log("MARK_ATTENDANCE", "EmployeeAttendance", attendance.getId(), null);
        return toResponse(attendance);
    }

    @Transactional
    public AttendanceResponse approve(UUID id) {
        EmployeeAttendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
        verifyGym(attendance.getEmployeeId());
        attendance.setApproved(true);
        return toResponse(attendanceRepository.save(attendance));
    }

    private Employee findEmployee(UUID id) {
        Employee e = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (!e.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Employee not found");
        }
        return e;
    }

    private void verifyGym(UUID employeeId) {
        findEmployee(employeeId);
    }

    private AttendanceResponse toResponse(EmployeeAttendance a) {
        Employee e = employeeRepository.findById(a.getEmployeeId()).orElse(null);
        return AttendanceResponse.builder()
                .id(a.getId())
                .employeeId(a.getEmployeeId())
                .employeeCode(e != null ? e.getEmployeeCode() : null)
                .employeeName(e != null ? e.getFirstName() + " " + e.getLastName() : "Unknown")
                .attendanceDate(a.getAttendanceDate())
                .checkIn(a.getCheckIn())
                .checkOut(a.getCheckOut())
                .status(a.getStatus())
                .source(a.getSource())
                .approved(a.isApproved())
                .build();
    }
}
