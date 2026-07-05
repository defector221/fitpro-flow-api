package com.fitpro.service;

import com.fitpro.domain.entity.InventoryItem;
import com.fitpro.domain.repository.InventoryItemRepository;
import com.fitpro.dto.common.PageResponse;
import com.fitpro.dto.inventory.*;
import com.fitpro.exception.ResourceNotFoundException;
import com.fitpro.security.SecurityUtils;
import com.fitpro.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public PageResponse<InventoryItemResponse> list(int page, int size) {
        UUID gymId = securityUtils.currentUser().getGymId();
        return PageMapper.toPageResponse(
                inventoryRepository.findByGymId(gymId, PageRequest.of(page, size)),
                this::toResponse);
    }

    public List<InventoryItemResponse> lowStock() {
        UUID gymId = securityUtils.currentUser().getGymId();
        return inventoryRepository.findLowStock(gymId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public InventoryItemResponse create(CreateInventoryItemRequest request) {
        UUID gymId = securityUtils.currentUser().getGymId();
        InventoryItem item = InventoryItem.builder()
                .gymId(gymId)
                .branchId(securityUtils.currentUser().getBranchId())
                .sku(request.getSku())
                .name(request.getName())
                .category(request.getCategory())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .reorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : 5)
                .active(true)
                .build();
        item = inventoryRepository.save(item);
        auditService.log("CREATE", "InventoryItem", item.getId(), null);
        return toResponse(item);
    }

    @Transactional
    public InventoryItemResponse adjustStock(UUID id, AdjustStockRequest request) {
        InventoryItem item = findItem(id);
        item.setQuantity(Math.max(0, item.getQuantity() + request.getQuantityDelta()));
        return toResponse(inventoryRepository.save(item));
    }

    private InventoryItem findItem(UUID id) {
        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        if (!item.getGymId().equals(securityUtils.currentUser().getGymId())) {
            throw new ResourceNotFoundException("Item not found");
        }
        return item;
    }

    private InventoryItemResponse toResponse(InventoryItem i) {
        return InventoryItemResponse.builder()
                .id(i.getId())
                .sku(i.getSku())
                .name(i.getName())
                .category(i.getCategory())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .reorderLevel(i.getReorderLevel())
                .lowStock(i.getQuantity() <= i.getReorderLevel())
                .active(i.isActive())
                .build();
    }
}
