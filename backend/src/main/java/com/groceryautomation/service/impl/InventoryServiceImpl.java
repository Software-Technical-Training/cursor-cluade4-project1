package com.groceryautomation.service.impl;

import com.groceryautomation.dto.response.CurrentInventoryResponse;
import com.groceryautomation.dto.response.InventoryItemResponse;
import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.enums.InventoryStatus;
import com.groceryautomation.repository.DeviceRepository;
import com.groceryautomation.repository.InventoryItemRepository;
import com.groceryautomation.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryItemRepository inventoryItemRepository;
    private final DeviceRepository deviceRepository;
    
    @Override
    @Transactional(readOnly = true)
    public CurrentInventoryResponse getCurrentInventory(Long userId) {
        // For POC, we'll get the first active device for the user
        List<Device> userDevices = deviceRepository.findByUserIdAndActive(userId, true);
        
        if (userDevices.isEmpty()) {
            log.warn("No active devices found for user: {}", userId);
            return CurrentInventoryResponse.builder()
                    .deviceOnline(false)
                    .items(List.of())
                    .totalItems(0)
                    .lowStockItems(0)
                    .outOfStockItems(0)
                    .build();
        }
        
        Device device = userDevices.get(0); // Get first device for POC
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByDeviceId(device.getId());
        
        List<InventoryItemResponse> itemResponses = inventoryItems.stream()
                .map(this::mapToInventoryItemResponse)
                .collect(Collectors.toList());
        
        int lowStockCount = (int) inventoryItems.stream()
                .filter(item -> item.getStatus() == InventoryStatus.LOW || 
                               item.getStatus() == InventoryStatus.CRITICAL)
                .count();
        
        int outOfStockCount = (int) inventoryItems.stream()
                .filter(item -> item.getStatus() == InventoryStatus.OUT_OF_STOCK)
                .count();
        
        return CurrentInventoryResponse.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getName())
                .deviceOnline(device.isOnline())
                .lastSync(device.getLastSync())
                .items(itemResponses)
                .totalItems(inventoryItems.size())
                .lowStockItems(lowStockCount)
                .outOfStockItems(outOfStockCount)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public InventoryItemResponse getInventoryItemById(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found: " + id));
        return mapToInventoryItemResponse(item);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getInventoryAlerts(Long userId) {
        List<InventoryStatus> alertStatuses = Arrays.asList(
                InventoryStatus.LOW, 
                InventoryStatus.CRITICAL, 
                InventoryStatus.OUT_OF_STOCK
        );
        
        return inventoryItemRepository.findByUserIdAndStatusIn(userId, alertStatuses).stream()
                .map(this::mapToInventoryItemResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getInventoryByStatus(Long userId, InventoryStatus status) {
        return inventoryItemRepository.findByUserIdAndStatusIn(userId, List.of(status)).stream()
                .map(this::mapToInventoryItemResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public InventoryItemResponse updateThreshold(Long inventoryItemId, Double newThreshold) {
        InventoryItem item = inventoryItemRepository.findById(inventoryItemId)
                .orElseThrow(() -> new RuntimeException("Inventory item not found: " + inventoryItemId));
        
        item.setThresholdQuantity(newThreshold);
        item.updateStatus(); // Recalculate status based on new threshold
        
        InventoryItem updatedItem = inventoryItemRepository.save(item);
        log.info("Updated threshold for item {} to {}", inventoryItemId, newThreshold);
        
        return mapToInventoryItemResponse(updatedItem);
    }
    
    @Override
    public void syncInventory(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));
        
        // In a real implementation, this would sync with actual sensor data
        // For POC, we'll just update the last sync time
        device.setLastSync(java.time.LocalDateTime.now());
        deviceRepository.save(device);
        
        log.info("Inventory synced for device: {}", deviceId);
    }
    
    private InventoryItemResponse mapToInventoryItemResponse(InventoryItem item) {
        return InventoryItemResponse.builder()
                .id(item.getId())
                .itemId(item.getGroceryItem().getId())
                .name(item.getGroceryItem().getName())
                .category(item.getGroceryItem().getCategory())
                .unit(item.getGroceryItem().getUnit())
                .quantity(item.getQuantity())
                .thresholdQuantity(item.getThresholdQuantity())
                .status(item.getStatus())
                .lastUpdated(item.getLastUpdated())
                .expirationDate(item.getExpirationDate())
                .imageUrl(item.getGroceryItem().getImageUrl())
                .build();
    }
} 