package com.fitpro.domain.repository;

import com.fitpro.domain.entity.GymSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GymSettingsRepository extends JpaRepository<GymSettings, UUID> {
    Optional<GymSettings> findByGymId(UUID gymId);
}
