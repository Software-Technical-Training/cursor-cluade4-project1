package com.groceryautomation.graphql.resolver.query;

import com.groceryautomation.dto.response.CurrentInventoryResponse;
import com.groceryautomation.dto.response.InventoryItemResponse;
import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.enums.InventoryStatus;
import com.groceryautomation.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class InventoryQueryResolver {
    
    private final InventoryService inventoryService;
    
    @QueryMapping
    public CurrentInventoryResponse currentInventory(@Argument Long deviceId) {
        log.info("Fetching current inventory for device: {} from GraphQL", deviceId);
        try {
            return inventoryService.getCurrentInventory(deviceId);
        } catch (Exception e) {
            log.error("Error fetching inventory for device {}: {}", deviceId, e.getMessage());
            return null;
        }
    }
    
    @QueryMapping
    public InventoryItem inventoryItem(@Argument Long id) {
        log.info("Fetching inventory item with ID: {} from GraphQL", id);
        try {
            InventoryItemResponse response = inventoryService.getInventoryItemById(id);
            return convertToEntity(response);
        } catch (Exception e) {
            log.error("Error fetching inventory item {}: {}", id, e.getMessage());
            return null;
        }
    }
    
    @QueryMapping
    public List<InventoryItem> inventoryByStatus(@Argument Long deviceId, @Argument InventoryStatus status) {
        log.info("Fetching inventory for device: {} with status: {} from GraphQL", deviceId, status);
        try {
            List<InventoryItemResponse> items;
            if (status != null) {
                items = inventoryService.getInventoryByStatus(deviceId, status);
            } else {
                // If no status specified, return all items
                CurrentInventoryResponse current = inventoryService.getCurrentInventory(deviceId);
                items = current.getItems();
            }
            
            return items.stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching inventory by status: {}", e.getMessage());
            return List.of();
        }
    }
    
    private InventoryItem convertToEntity(InventoryItemResponse response) {
        return InventoryItem.builder()
                .id(response.getId())
                .quantity(response.getQuantity())
                .thresholdQuantity(response.getThresholdQuantity())
                .status(response.getStatus())
                .lastUpdated(response.getLastUpdated())
                .expirationDate(response.getExpirationDate())
                .build();
    }
}