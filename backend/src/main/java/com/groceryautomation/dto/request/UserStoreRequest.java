package com.groceryautomation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStoreRequest {
    
    @NotNull(message = "Store ID is required")
    private Long storeId;
    
    @Min(value = 1, message = "Priority must be at least 1")
    private Integer priority; // Optional, will be auto-assigned if not provided
    
    private Double maxDeliveryFee;
    
    private Double maxDistanceMiles;
    
    private String notes;
    
    @Builder.Default
    private boolean isActive = true;
} 