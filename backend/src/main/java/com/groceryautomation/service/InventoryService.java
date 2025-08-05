package com.groceryautomation.service;

import com.groceryautomation.dto.response.CurrentInventoryResponse;
import com.groceryautomation.dto.response.InventoryItemResponse;
import com.groceryautomation.enums.InventoryStatus;

import java.util.List;

public interface InventoryService {
    
    CurrentInventoryResponse getCurrentInventory(Long userId);
    
    InventoryItemResponse getInventoryItemById(Long id);
    
    List<InventoryItemResponse> getInventoryAlerts(Long userId);
    
    List<InventoryItemResponse> getInventoryByStatus(Long userId, InventoryStatus status);
    
    InventoryItemResponse updateThreshold(Long inventoryItemId, Double newThreshold);
    
    void syncInventory(String deviceId);
} 