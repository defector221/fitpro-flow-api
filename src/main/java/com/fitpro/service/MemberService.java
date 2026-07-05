package com.fitpro.service;

import com.fitpro.domain.entity.Member;
import com.fitpro.domain.entity.Membership;
import com.fitpro.domain.entity.MembershipPlan;
import com.fitpro.domain.enums.MemberStatus;
import com.fitpro.domain.enums.MembershipStatus;
import com.fitpro.domain.repository.MemberRepository;
import com.fitpro.domain.repository.MembershipPlanRepository;
import com.fitpro.domain.repository.MembershipRepository;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.member.CreateMemberRequest;
import com.fitpro.dto.member.MemberResponse;
import com.fitpro.dto.member.UpdateMemberRequest;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public PageResponse<MemberResponse> list(String search, MemberStatus status, int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        Page<Member> result = memberRepository.search(
                gymId, blankToNull(search), status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PageMapper.toPageResponse(result, this::toResponse);
    }

    public MemberResponse getById(UUID id) {
        return toResponse(findMember(id));
    }

    @Transactional
    public MemberResponse create(CreateMemberRequest request) {
        var user = securityUtils.currentUser();
        UUID gymId = user.getGymId();
        UUID branchId = request.getBranchId() != null ? request.getBranchId() : user.getBranchId();

        String memberCode = generateMemberCode(gymId);

        Member member = Member.builder()
                .gymId(gymId)
                .branchId(branchId)
                .memberCode(memberCode)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .address(request.getAddress())
                .emergencyName(request.getEmergencyName())
                .emergencyPhone(request.getEmergencyPhone())
                .heightCm(request.getHeightCm())
                .weightKg(request.getWeightKg())
                .fitnessGoals(request.getFitnessGoals())
                .medicalNotes(request.getMedicalNotes())
                .status(MemberStatus.ACTIVE)
                .build();

        member = memberRepository.save(member);

        if (request.getPlanId() != null) {
            createMembership(member.getId(), request.getPlanId());
        }

        auditService.log("CREATE", "Member", member.getId(), Map.of("memberCode", memberCode));
        return toResponse(member);
    }

    @Transactional
    public MemberResponse update(UUID id, UpdateMemberRequest request) {
        Member member = findMember(id);
        if (request.getFirstName() != null) member.setFirstName(request.getFirstName());
        if (request.getLastName() != null) member.setLastName(request.getLastName());
        if (request.getEmail() != null) member.setEmail(request.getEmail());
        if (request.getPhone() != null) member.setPhone(request.getPhone());
        if (request.getDateOfBirth() != null) member.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) member.setGender(request.getGender());
        if (request.getAddress() != null) member.setAddress(request.getAddress());
        if (request.getEmergencyName() != null) member.setEmergencyName(request.getEmergencyName());
        if (request.getEmergencyPhone() != null) member.setEmergencyPhone(request.getEmergencyPhone());
        if (request.getHeightCm() != null) member.setHeightCm(request.getHeightCm());
        if (request.getWeightKg() != null) member.setWeightKg(request.getWeightKg());
        if (request.getFitnessGoals() != null) member.setFitnessGoals(request.getFitnessGoals());
        if (request.getMedicalNotes() != null) member.setMedicalNotes(request.getMedicalNotes());
        if (request.getStatus() != null) member.setStatus(request.getStatus());

        member = memberRepository.save(member);
        auditService.log("UPDATE", "Member", member.getId(), Map.of("status", member.getStatus().name()));
        return toResponse(member);
    }

    private void createMembership(UUID memberId, UUID planId) {
        MembershipPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        LocalDate start = LocalDate.now();
        Membership membership = Membership.builder()
                .memberId(memberId)
                .planId(plan.getId())
                .startDate(start)
                .endDate(start.plusDays(plan.getDurationDays()))
                .amountPaid(plan.getPrice())
                .status(MembershipStatus.ACTIVE)
                .build();
        membershipRepository.save(membership);
    }

    private Member findMember(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        UUID gymId = securityUtils.currentUser().getGymId();
        if (!member.getGymId().equals(gymId)) {
            throw new ResourceNotFoundException("Member not found");
        }
        return member;
    }

    private String generateMemberCode(UUID gymId) {
        long count = memberRepository.findByGymId(gymId, PageRequest.of(0, 1)).getTotalElements();
        return String.format("MEM%05d", count + 1);
    }

    private MemberResponse toResponse(Member member) {
        List<Membership> memberships = membershipRepository.findByMemberIdOrderByStartDateDesc(member.getId());
        String planName = null;
        LocalDate endDate = null;
        if (!memberships.isEmpty()) {
            Membership active = memberships.get(0);
            planName = planRepository.findById(active.getPlanId()).map(MembershipPlan::getName).orElse(null);
            endDate = active.getEndDate();
        }

        BigDecimal bmi = null;
        if (member.getHeightCm() != null && member.getWeightKg() != null
                && member.getHeightCm().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightM = member.getHeightCm().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            bmi = member.getWeightKg().divide(heightM.multiply(heightM), 1, RoundingMode.HALF_UP);
        }

        return MemberResponse.builder()
                .id(member.getId())
                .memberCode(member.getMemberCode())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .dateOfBirth(member.getDateOfBirth())
                .gender(member.getGender())
                .address(member.getAddress())
                .emergencyName(member.getEmergencyName())
                .emergencyPhone(member.getEmergencyPhone())
                .heightCm(member.getHeightCm())
                .weightKg(member.getWeightKg())
                .bmi(bmi)
                .fitnessGoals(member.getFitnessGoals())
                .medicalNotes(member.getMedicalNotes())
                .status(member.getStatus())
                .branchId(member.getBranchId())
                .activePlanName(planName)
                .membershipEndDate(endDate)
                .createdAt(member.getCreatedAt())
                .build();
    }

    private String blankToNull(String s) {
        return s == null || s.isBlank() ? "" : s.trim();
    }
}
