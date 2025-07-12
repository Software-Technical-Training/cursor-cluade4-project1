package com.groceryautomation.dto.response;

import com.groceryautomation.enums.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemResponse {
    
    private Long id;
    private Long itemId;
    private String name;
    private String category;
    private String unit;
    private Double quantity;
    private Double thresholdQuantity;
    private InventoryStatus status;
    private LocalDateTime lastUpdated;
    private LocalDateTime expirationDate;
    private String imageUrl;
} 