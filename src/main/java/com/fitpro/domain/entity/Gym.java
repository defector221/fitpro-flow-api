package com.fitpro.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "gyms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gym extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;

    @Builder.Default
    private String country = "India";

    private String pincode;
    private String gstin;

    @Column(name = "logo_url")
    private String logoUrl;

    @Builder.Default
    private String timezone = "Asia/Kolkata";

    @Builder.Default
    private String currency = "INR";

    @Builder.Default
    private boolean active = true;
}
