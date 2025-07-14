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
public class DeviceResponse {
    
    private Long id;
    private String deviceId;
    private String name;
    private Long userId;
    private String userEmail;
    private boolean active;
    private boolean online;
    private LocalDateTime lastSync;
    private LocalDateTime createdAt;
    private Integer inventoryItemCount;
} 