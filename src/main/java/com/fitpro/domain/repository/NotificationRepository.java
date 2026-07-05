package com.fitpro.domain.repository;

import com.fitpro.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByGymIdOrderByCreatedAtDesc(UUID gymId, Pageable pageable);
    long countByGymIdAndReadAtIsNull(UUID gymId);
}
