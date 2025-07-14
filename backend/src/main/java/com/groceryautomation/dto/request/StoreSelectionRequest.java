package com.groceryautomation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSelectionRequest {
    
    // Store details from Google Maps
    @NotBlank(message = "Store name is required")
    private String name;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    private String phone;
    private String googlePlaceId; // To check if store already exists
    
    // User preferences for this store
    @Min(value = 1, message = "Priority must be at least 1")
    private Integer priority; // Optional, will be auto-assigned if not provided
    
    private Double maxDeliveryFee;
    private Double maxDistanceMiles;
    private String notes;
    
    @Builder.Default
    private boolean isActive = true;
} 