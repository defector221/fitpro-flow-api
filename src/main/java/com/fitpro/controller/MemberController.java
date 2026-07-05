package com.fitpro.controller;

import com.fitpro.domain.enums.MemberStatus;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.member.CreateMemberRequest;
import com.fitpro.dto.member.MemberResponse;
import com.fitpro.dto.member.UpdateMemberRequest;
import com.fitpro.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "List members with pagination and search")
    public ResponseEntity<PageResponse<MemberResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(memberService.list(search, status, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    public ResponseEntity<MemberResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(memberService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Register a new member")
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody CreateMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update member")
    public ResponseEntity<MemberResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(memberService.update(id, request));
    }
}
