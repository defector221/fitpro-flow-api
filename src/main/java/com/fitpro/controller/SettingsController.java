package com.fitpro.controller;

import com.fitpro.dto.settings.*;
import com.fitpro.service.SettingsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(name = "Settings")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public SettingsResponse get() {
        return settingsService.getSettings();
    }

    @PutMapping("/gym")
    public GymProfileResponse updateGym(@RequestBody UpdateGymRequest request) {
        return settingsService.updateGym(request);
    }

    @PutMapping
    public SettingsResponse updateSettings(@RequestBody UpdateSettingsRequest request) {
        return settingsService.updateSettings(request);
    }
}
