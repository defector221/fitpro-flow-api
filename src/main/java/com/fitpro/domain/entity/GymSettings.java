package com.fitpro.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "gym_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GymSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "gym_id", nullable = false, unique = true)
    private UUID gymId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "working_hours", columnDefinition = "jsonb")
    private Map<String, Object> workingHours;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "membership_rules", columnDefinition = "jsonb")
    private Map<String, Object> membershipRules;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tax_config", columnDefinition = "jsonb")
    private Map<String, Object> taxConfig;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notification_config", columnDefinition = "jsonb")
    private Map<String, Object> notificationConfig;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_config", columnDefinition = "jsonb")
    private Map<String, Object> paymentConfig;

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();
}
