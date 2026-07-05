package com.fitpro.service;

import com.fitpro.domain.entity.*;
import com.fitpro.domain.repository.*;
import com.fitpro.dto.trainer.*;
import com.fitpro.exception.BusinessException;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainerAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public List<TrainerResponse> list() {
        UUID gymId = securityUtils.currentUser().getGymId();
        return trainerRepository.findByGym(gymId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public TrainerResponse create(CreateTrainerRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (!employee.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Employee not found");
        }
        if (trainerRepository.findByEmployeeId(employee.getId()).isPresent()) {
            throw new BusinessException("Employee is already a trainer");
        }

        Trainer trainer = Trainer.builder()
                .employeeId(employee.getId())
                .specialization(request.getSpecialization())
                .bio(request.getBio())
                .active(true)
                .build();
        trainer = trainerRepository.save(trainer);
        auditService.log("CREATE", "Trainer", trainer.getId(), null);
        return toResponse(trainer);
    }

    @Transactional
    public AssignmentResponse assignMember(UUID trainerId, AssignMemberRequest request) {
        Trainer trainer = findTrainer(trainerId);
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        if (!member.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Member not found");
        }

        TrainerAssignment assignment = TrainerAssignment.builder()
                .trainerId(trainer.getId())
                .memberId(member.getId())
                .startDate(request.getStartDate())
                .active(true)
                .build();
        assignment = assignmentRepository.save(assignment);
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .memberId(member.getId())
                .memberName(member.getFirstName() + " " + member.getLastName())
                .startDate(assignment.getStartDate())
                .active(assignment.isActive())
                .build();
    }

    private Trainer findTrainer(UUID id) {
        Trainer t = trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found"));
        Employee e = employeeRepository.findById(t.getEmployeeId()).orElseThrow();
        if (!e.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Trainer not found");
        }
        return t;
    }

    private TrainerResponse toResponse(Trainer t) {
        Employee e = employeeRepository.findById(t.getEmployeeId()).orElse(null);
        List<TrainerAssignment> assignments = assignmentRepository.findByTrainerIdAndActiveTrue(t.getId());
        List<AssignmentResponse> assignmentResponses = assignments.stream().map(a -> {
            Member m = memberRepository.findById(a.getMemberId()).orElse(null);
            return AssignmentResponse.builder()
                    .id(a.getId())
                    .memberId(a.getMemberId())
                    .memberName(m != null ? m.getFirstName() + " " + m.getLastName() : "Unknown")
                    .startDate(a.getStartDate())
                    .active(a.isActive())
                    .build();
        }).toList();

        return TrainerResponse.builder()
                .id(t.getId())
                .employeeId(t.getEmployeeId())
                .employeeCode(e != null ? e.getEmployeeCode() : null)
                .name(e != null ? e.getFirstName() + " " + e.getLastName() : "Unknown")
                .specialization(t.getSpecialization())
                .bio(t.getBio())
                .active(t.isActive())
                .assignedMembers(assignments.size())
                .assignments(assignmentResponses)
                .build();
    }
}
