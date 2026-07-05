package com.fitpro.controller;

import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.inventory.*;
import com.fitpro.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public PageResponse<InventoryItemResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return inventoryService.list(page, size);
    }

    @GetMapping("/low-stock")
    public List<InventoryItemResponse> lowStock() {
        return inventoryService.lowStock();
    }

    @PostMapping
    public ResponseEntity<InventoryItemResponse> create(@Valid @RequestBody CreateInventoryItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.create(request));
    }

    @PatchMapping("/{id}/stock")
    public InventoryItemResponse adjustStock(@PathVariable UUID id, @Valid @RequestBody AdjustStockRequest request) {
        return inventoryService.adjustStock(id, request);
    }
}
