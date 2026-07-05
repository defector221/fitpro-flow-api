package com.fitpro.service;

import com.fitpro.domain.entity.MembershipPlan;
import com.fitpro.domain.repository.MembershipPlanRepository;
import com.fitpro.dto.plan.CreatePlanRequest;
import com.fitpro.dto.plan.MembershipPlanResponse;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;

    public List<MembershipPlanResponse> listAll() {
        UUID gymId = securityUtils.currentUser().getGymId();
        return planRepository.findByGymIdOrderByPriceAsc(gymId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MembershipPlanResponse> listActive() {
        UUID gymId = securityUtils.currentUser().getGymId();
        return planRepository.findByGymIdAndActiveTrueOrderByPriceAsc(gymId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MembershipPlanResponse create(CreatePlanRequest request) {
        UUID gymId = securityUtils.currentUser().getGymId();
        MembershipPlan plan = MembershipPlan.builder()
                .gymId(gymId)
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .durationDays(request.getDurationDays())
                .price(request.getPrice())
                .planType(request.getPlanType() != null ? request.getPlanType() : "GENERAL")
                .active(true)
                .build();
        return toResponse(planRepository.save(plan));
    }

    public MembershipPlanResponse getById(UUID id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
        if (!plan.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Plan not found");
        }
        return toResponse(plan);
    }

    private MembershipPlanResponse toResponse(MembershipPlan plan) {
        return MembershipPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .code(plan.getCode())
                .description(plan.getDescription())
                .durationDays(plan.getDurationDays())
                .price(plan.getPrice())
                .planType(plan.getPlanType())
                .active(plan.isActive())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
