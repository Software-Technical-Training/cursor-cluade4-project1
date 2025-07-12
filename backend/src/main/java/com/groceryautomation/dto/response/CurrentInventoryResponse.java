package com.groceryautomation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentInventoryResponse {
    
    private String deviceId;
    private String deviceName;
    private boolean deviceOnline;
    private LocalDateTime lastSync;
    private List<InventoryItemResponse> items;
    private int totalItems;
    private int lowStockItems;
    private int outOfStockItems;
} 