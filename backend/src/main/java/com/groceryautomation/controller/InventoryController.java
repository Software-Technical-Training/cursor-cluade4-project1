package com.groceryautomation.controller;

import com.groceryautomation.dto.response.ApiResponse;
import com.groceryautomation.dto.response.CurrentInventoryResponse;
import com.groceryautomation.dto.response.InventoryItemResponse;
import com.groceryautomation.enums.InventoryStatus;
import com.groceryautomation.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory Management", description = "Endpoints for viewing and managing inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @GetMapping("/current/{userId}")
    @Operation(summary = "Get current inventory", description = "Get all inventory items for a user's device")
    public ResponseEntity<ApiResponse<CurrentInventoryResponse>> getCurrentInventory(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Fetching current inventory for user: {}", userId);
        CurrentInventoryResponse inventory = inventoryService.getCurrentInventory(userId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }
    
    @GetMapping("/alerts/{userId}")
    @Operation(summary = "Get inventory alerts", description = "Get items that need restocking (low, critical, or out of stock)")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> getInventoryAlerts(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Fetching inventory alerts for user: {}", userId);
        List<InventoryItemResponse> alerts = inventoryService.getInventoryAlerts(userId);
        return ResponseEntity.ok(ApiResponse.success(alerts, 
                alerts.isEmpty() ? "No alerts at this time" : alerts.size() + " items need attention"));
    }
    
    @GetMapping("/status/{userId}")
    @Operation(summary = "Get inventory by status", description = "Filter inventory items by their status")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> getInventoryByStatus(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Inventory status") @RequestParam InventoryStatus status) {
        log.info("Fetching inventory with status {} for user: {}", status, userId);
        List<InventoryItemResponse> items = inventoryService.getInventoryByStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(items));
    }
    
    @PutMapping("/threshold/{itemId}")
    @Operation(summary = "Update item threshold", description = "Update the low-stock threshold for an inventory item")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> updateThreshold(
            @Parameter(description = "Inventory item ID") @PathVariable Long itemId,
            @Parameter(description = "New threshold value") @RequestParam Double threshold) {
        log.info("Updating threshold for item {} to {}", itemId, threshold);
        try {
            InventoryItemResponse updated = inventoryService.updateThreshold(itemId, threshold);
            return ResponseEntity.ok(ApiResponse.success(updated, "Threshold updated successfully"));
        } catch (RuntimeException e) {
            log.error("Error updating threshold: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/sync/{deviceId}")
    @Operation(summary = "Sync inventory", description = "Manually trigger inventory sync for a device")
    public ResponseEntity<ApiResponse<Void>> syncInventory(
            @Parameter(description = "Device ID") @PathVariable String deviceId) {
        log.info("Syncing inventory for device: {}", deviceId);
        try {
            inventoryService.syncInventory(deviceId);
            return ResponseEntity.ok(ApiResponse.success(null, "Inventory sync initiated"));
        } catch (RuntimeException e) {
            log.error("Error syncing inventory: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 