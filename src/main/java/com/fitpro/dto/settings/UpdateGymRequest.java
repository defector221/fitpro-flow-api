package com.fitpro.dto.settings;

import lombok.Data;

@Data
public class UpdateGymRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String gstin;
}
