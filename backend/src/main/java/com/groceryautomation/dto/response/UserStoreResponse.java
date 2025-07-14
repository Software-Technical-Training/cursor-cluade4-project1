package com.groceryautomation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStoreResponse {
    
    private Long id;
    private Long userId;
    private String userEmail;
    private StoreResponse store;
    private Integer priority;
    private boolean isPrimary;
    private boolean isActive;
    private Double maxDeliveryFee;
    private Double maxDistanceMiles;
    private String notes;
    private LocalDateTime addedAt;
} 