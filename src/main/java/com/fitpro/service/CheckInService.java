package com.fitpro.service;

import com.fitpro.domain.entity.*;
import com.fitpro.domain.enums.MemberStatus;
import com.fitpro.domain.repository.*;
import com.fitpro.dto.checkin.MemberCheckInRequest;
import com.fitpro.dto.checkin.MemberCheckInResponse;
import com.fitpro.dto.checkin.MemberCheckInScanRequest;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.exception.BusinessException;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final MemberCheckInRepository checkInRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository planRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;
    private final NotificationService notificationService;

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    public PageResponse<MemberCheckInResponse> listToday(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        var start = Instant.now().atZone(ZONE).toLocalDate().atStartOfDay(ZONE).toInstant();
        var pageResult = checkInRepository.findRecentByGym(gymId, PageRequest.of(page, size));
        return PageMapper.toPageResponse(pageResult, c -> toResponse(c, start));
    }

    public PageResponse<MemberCheckInResponse> listAll(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                checkInRepository.findRecentByGym(gymId, PageRequest.of(page, size)),
                c -> toResponse(c, null));
    }

    @Transactional
    public MemberCheckInResponse checkIn(MemberCheckInRequest request) {
        var user = securityUtils.currentUser();
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        if (!member.getGymId().equals(user.getGymId())) {
            throw new ResourceNotFoundException("Member not found");
        }
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new BusinessException("Member is not active");
        }

        MemberCheckIn checkIn = MemberCheckIn.builder()
                .memberId(member.getId())
                .branchId(user.getBranchId())
                .source(request.getSource() != null ? request.getSource() : "QR")
                .checkInAt(Instant.now())
                .build();
        checkIn = checkInRepository.save(checkIn);
        auditService.log("CHECK_IN", "MemberCheckIn", checkIn.getId(), null);
        notificationService.memberCheckIn(member, checkIn.getBranchId(), checkIn.getId());
        return toResponse(checkIn, null);
    }

    @Transactional
    public MemberCheckInResponse scan(MemberCheckInScanRequest request) {
        var user = securityUtils.currentUser();
        Member member = memberRepository.findByGymIdAndMemberCode(user.getGymId(), request.getMemberCode().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if ("CHECK_OUT".equalsIgnoreCase(request.getAction())) {
            MemberCheckIn openCheckIn = checkInRepository.findLatestOpenByMemberId(member.getId())
                    .orElseThrow(() -> new BusinessException("No open check-in found for member"));
            return checkOut(openCheckIn.getId());
        }

        MemberCheckInRequest checkInRequest = new MemberCheckInRequest();
        checkInRequest.setMemberId(member.getId());
        checkInRequest.setSource(request.getSource() != null ? request.getSource() : "ID_CARD");
        return checkIn(checkInRequest);
    }

    @Transactional
    public MemberCheckInResponse checkOut(UUID checkInId) {
        MemberCheckIn checkIn = checkInRepository.findById(checkInId)
                .orElseThrow(() -> new ResourceNotFoundException("Check-in not found"));
        Member member = memberRepository.findById(checkIn.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        if (!member.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Check-in not found");
        }
        if (checkIn.getCheckOutAt() != null) {
            throw new BusinessException("Member is already checked out");
        }
        checkIn.setCheckOutAt(Instant.now());
        checkIn = checkInRepository.save(checkIn);
        auditService.log("CHECK_OUT", "MemberCheckIn", checkIn.getId(), null);
        notificationService.memberCheckOut(member, checkIn.getBranchId(), checkIn.getId());
        return toResponse(checkIn, null);
    }

    private MemberCheckInResponse toResponse(MemberCheckIn c, Instant dayStart) {
        Member member = memberRepository.findById(c.getMemberId()).orElse(null);
        String planName = "—";
        if (member != null) {
            var memberships = membershipRepository.findByMemberIdOrderByStartDateDesc(member.getId());
            if (!memberships.isEmpty()) {
                planName = planRepository.findById(memberships.get(0).getPlanId())
                        .map(MembershipPlan::getName).orElse("—");
            }
        }
        return MemberCheckInResponse.builder()
                .id(c.getId())
                .memberId(c.getMemberId())
                .memberCode(member != null ? member.getMemberCode() : null)
                .memberName(member != null ? member.getFirstName() + " " + member.getLastName() : "Unknown")
                .planName(planName)
                .checkInAt(c.getCheckInAt())
                .checkOutAt(c.getCheckOutAt())
                .source(c.getSource())
                .build();
    }
}
