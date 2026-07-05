package com.fitpro.dto.settings;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GymProfileResponse {
    private UUID id;
    private String name;
    private String slug;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String gstin;
    private String timezone;
    private String currency;
}
