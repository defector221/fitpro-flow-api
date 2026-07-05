package com.fitpro.dto.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdjustStockRequest {
    @NotNull
    private Integer quantityDelta;
    private String reason;
}
