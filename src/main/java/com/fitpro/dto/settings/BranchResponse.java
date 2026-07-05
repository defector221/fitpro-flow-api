package com.fitpro.dto.settings;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BranchResponse {
    private UUID id;
    private String name;
    private String code;
    private String city;
    private boolean headOffice;
    private boolean active;
}
