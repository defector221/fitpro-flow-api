package com.fitpro.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "gym_id", nullable = false)
    private UUID gymId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "member_id")
    private UUID memberId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 50)
    @Builder.Default
    private String paymentMethod = "CASH";

    @Column(name = "payment_type", nullable = false, length = 50)
    @Builder.Default
    private String paymentType = "MEMBERSHIP";

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "COMPLETED";

    @Column(name = "reference_no")
    private String referenceNo;

    private String notes;

    @Column(name = "paid_at", nullable = false)
    @Builder.Default
    private Instant paidAt = Instant.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
