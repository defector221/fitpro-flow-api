package com.fitpro.domain.entity;

import com.fitpro.domain.enums.LeadStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "gym_id", nullable = false)
    private UUID gymId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(nullable = false, length = 200)
    private String name;

    private String email;
    private String phone;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String source = "WALK_IN";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    private String notes;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "converted_member_id")
    private UUID convertedMemberId;
}
