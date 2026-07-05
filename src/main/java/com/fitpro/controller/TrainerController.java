package com.fitpro.controller;

import com.fitpro.dto.trainer.*;
import com.fitpro.service.TrainerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    public List<TrainerResponse> list() {
        return trainerService.list();
    }

    @PostMapping
    public ResponseEntity<TrainerResponse> create(@Valid @RequestBody CreateTrainerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.create(request));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<AssignmentResponse> assign(
            @PathVariable UUID id,
            @Valid @RequestBody AssignMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.assignMember(id, request));
    }
}
