package com.fitpro.service;

import com.fitpro.domain.entity.Branch;
import com.fitpro.domain.entity.Gym;
import com.fitpro.domain.entity.GymSettings;
import com.fitpro.domain.repository.BranchRepository;
import com.fitpro.domain.repository.GymRepository;
import com.fitpro.domain.repository.GymSettingsRepository;
import com.fitpro.dto.settings.*;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final GymRepository gymRepository;
    private final BranchRepository branchRepository;
    private final GymSettingsRepository settingsRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public SettingsResponse getSettings() {
        UUID gymId = securityUtils.currentUser().getGymId();
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found"));
        GymSettings settings = settingsRepository.findByGymId(gymId).orElse(null);

        return SettingsResponse.builder()
                .gym(toGymProfile(gym))
                .branches(branchRepository.findByGymIdAndActiveTrue(gymId).stream()
                        .map(this::toBranch).toList())
                .workingHours(settings != null ? settings.getWorkingHours() : null)
                .taxConfig(settings != null ? settings.getTaxConfig() : null)
                .notificationConfig(settings != null ? settings.getNotificationConfig() : null)
                .paymentConfig(settings != null ? settings.getPaymentConfig() : null)
                .build();
    }

    @Transactional
    public GymProfileResponse updateGym(UpdateGymRequest request) {
        UUID gymId = securityUtils.currentUser().getGymId();
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found"));
        if (request.getName() != null) gym.setName(request.getName());
        if (request.getEmail() != null) gym.setEmail(request.getEmail());
        if (request.getPhone() != null) gym.setPhone(request.getPhone());
        if (request.getAddress() != null) gym.setAddress(request.getAddress());
        if (request.getCity() != null) gym.setCity(request.getCity());
        if (request.getState() != null) gym.setState(request.getState());
        if (request.getGstin() != null) gym.setGstin(request.getGstin());
        gym = gymRepository.save(gym);
        auditService.log("UPDATE", "Gym", gym.getId(), null);
        return toGymProfile(gym);
    }

    @Transactional
    public SettingsResponse updateSettings(UpdateSettingsRequest request) {
        UUID gymId = securityUtils.currentUser().getGymId();
        GymSettings settings = settingsRepository.findByGymId(gymId)
                .orElse(GymSettings.builder().gymId(gymId).build());
        if (request.getWorkingHours() != null) settings.setWorkingHours(request.getWorkingHours());
        if (request.getTaxConfig() != null) settings.setTaxConfig(request.getTaxConfig());
        if (request.getNotificationConfig() != null) settings.setNotificationConfig(request.getNotificationConfig());
        if (request.getPaymentConfig() != null) settings.setPaymentConfig(request.getPaymentConfig());
        settings.setUpdatedAt(Instant.now());
        settingsRepository.save(settings);
        return getSettings();
    }

    private GymProfileResponse toGymProfile(Gym g) {
        return GymProfileResponse.builder()
                .id(g.getId()).name(g.getName()).slug(g.getSlug())
                .email(g.getEmail()).phone(g.getPhone()).address(g.getAddress())
                .city(g.getCity()).state(g.getState()).gstin(g.getGstin())
                .timezone(g.getTimezone()).currency(g.getCurrency())
                .build();
    }

    private BranchResponse toBranch(Branch b) {
        return BranchResponse.builder()
                .id(b.getId()).name(b.getName()).code(b.getCode())
                .city(b.getCity()).headOffice(b.isHeadOffice()).active(b.isActive())
                .build();
    }
}
