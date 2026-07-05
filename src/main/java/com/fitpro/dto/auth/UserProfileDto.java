package com.fitpro.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserProfileDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UUID gymId;
    private UUID branchId;
    private List<String> roles;
    private List<String> permissions;
}
