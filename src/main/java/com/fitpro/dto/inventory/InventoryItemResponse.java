package com.fitpro.dto.inventory;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InventoryItemResponse {
    private UUID id;
    private String sku;
    private String name;
    private String category;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Integer reorderLevel;
    private boolean lowStock;
    private boolean active;
}
