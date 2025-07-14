package com.groceryautomation.dto.request;

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
public class DeviceRegistrationRequest {
    
    @NotBlank(message = "Device ID is required")
    private String deviceId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String name; // Optional custom name, defaults to "Smart Fridge Sensor"
} 