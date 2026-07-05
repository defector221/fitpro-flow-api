package com.fitpro.service;

import com.fitpro.domain.entity.Lead;
import com.fitpro.domain.enums.LeadStatus;
import com.fitpro.domain.repository.LeadRepository;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.crm.*;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public CrmSummaryResponse summary() {
        UUID gymId = securityUtils.currentUser().getGymId();
        long total = leadRepository.findByGymId(gymId, PageRequest.of(0, 1)).getTotalElements();
        long won = leadRepository.countByGymIdAndStatus(gymId, LeadStatus.WON);
        long lost = leadRepository.countByGymIdAndStatus(gymId, LeadStatus.LOST);
        long newLeads = leadRepository.countByGymIdAndStatus(gymId, LeadStatus.NEW);
        double rate = total > 0 ? (won * 100.0 / total) : 0;

        return CrmSummaryResponse.builder()
                .totalLeads(total)
                .newLeads(newLeads)
                .wonLeads(won)
                .lostLeads(lost)
                .conversionRate(Math.round(rate * 10) / 10.0)
                .build();
    }

    public PageResponse<LeadResponse> list(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                leadRepository.findByGymId(gymId, PageRequest.of(page, size)),
                this::toResponse);
    }

    @Transactional
    public LeadResponse create(CreateLeadRequest request) {
        var user = securityUtils.currentUser();
        Lead lead = Lead.builder()
                .gymId(user.getGymId())
                .branchId(user.getBranchId())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .source(request.getSource() != null ? request.getSource() : "WALK_IN")
                .status(LeadStatus.NEW)
                .notes(request.getNotes())
                .followUpDate(request.getFollowUpDate())
                .assignedTo(request.getAssignedTo())
                .build();
        lead = leadRepository.save(lead);
        auditService.log("CREATE", "Lead", lead.getId(), null);
        return toResponse(lead);
    }

    @Transactional
    public LeadResponse update(UUID id, UpdateLeadRequest request) {
        Lead lead = findLead(id);
        if (request.getName() != null) lead.setName(request.getName());
        if (request.getEmail() != null) lead.setEmail(request.getEmail());
        if (request.getPhone() != null) lead.setPhone(request.getPhone());
        if (request.getStatus() != null) lead.setStatus(request.getStatus());
        if (request.getNotes() != null) lead.setNotes(request.getNotes());
        if (request.getFollowUpDate() != null) lead.setFollowUpDate(request.getFollowUpDate());
        return toResponse(leadRepository.save(lead));
    }

    private Lead findLead(UUID id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        if (!lead.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Lead not found");
        }
        return lead;
    }

    private LeadResponse toResponse(Lead l) {
        return LeadResponse.builder()
                .id(l.getId())
                .name(l.getName())
                .email(l.getEmail())
                .phone(l.getPhone())
                .source(l.getSource())
                .status(l.getStatus())
                .notes(l.getNotes())
                .followUpDate(l.getFollowUpDate())
                .convertedMemberId(l.getConvertedMemberId())
                .build();
    }
}
