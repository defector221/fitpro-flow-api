package com.fitpro.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "member_check_ins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "check_in_at", nullable = false)
    @Builder.Default
    private Instant checkInAt = Instant.now();

    @Column(name = "check_out_at")
    private Instant checkOutAt;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String source = "QR";

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
