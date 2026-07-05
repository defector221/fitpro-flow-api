package com.fitpro.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateInventoryItemRequest {
    @NotBlank
    private String sku;
    @NotBlank
    private String name;
    @NotBlank
    private String category;
    @NotNull
    private Integer quantity;
    private BigDecimal unitPrice;
    private Integer reorderLevel;
}
