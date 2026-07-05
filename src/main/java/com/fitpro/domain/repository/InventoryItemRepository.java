package com.fitpro.domain.repository;

import com.fitpro.domain.entity.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    Page<InventoryItem> findByGymId(UUID gymId, Pageable pageable);

    @Query("SELECT i FROM InventoryItem i WHERE i.gymId = :gymId AND i.quantity <= i.reorderLevel AND i.active = true")
    List<InventoryItem> findLowStock(@Param("gymId") UUID gymId);
}
