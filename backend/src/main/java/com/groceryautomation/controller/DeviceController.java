package com.groceryautomation.controller;

import com.groceryautomation.dto.request.DeviceRegistrationRequest;
import com.groceryautomation.dto.response.ApiResponse;
import com.groceryautomation.dto.response.DeviceResponse;
import com.groceryautomation.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Device Management", description = "Endpoints for device registration and management")
public class DeviceController {
    
    private final DeviceService deviceService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new device", description = "Register a new device for a user")
    public ResponseEntity<ApiResponse<DeviceResponse>> registerDevice(
            @Valid @RequestBody DeviceRegistrationRequest request) {
        log.info("Registering new device {} for user {}", request.getDeviceId(), request.getUserId());
        try {
            DeviceResponse device = deviceService.registerDevice(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(device, "Device registered successfully"));
        } catch (RuntimeException e) {
            log.error("Error registering device: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user devices", description = "Get all devices registered for a user")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getUserDevices(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Fetching devices for user: {}", userId);
        List<DeviceResponse> devices = deviceService.getUserDevices(userId);
        return ResponseEntity.ok(ApiResponse.success(devices));
    }
    
    @GetMapping("/{deviceId}")
    @Operation(summary = "Get device by device ID", description = "Get device information by device ID string (e.g. 'FRIDGE-001', not the numeric database ID)")
    public ResponseEntity<ApiResponse<DeviceResponse>> getDevice(
            @Parameter(description = "Device ID string (e.g. FRIDGE-001)", example = "FRIDGE-001") @PathVariable String deviceId) {
        log.info("Fetching device: {}", deviceId);
        try {
            DeviceResponse device = deviceService.getDeviceById(deviceId);
            return ResponseEntity.ok(ApiResponse.success(device));
        } catch (RuntimeException e) {
            log.error("Error fetching device: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{deviceId}/status")
    @Operation(summary = "Update device status", description = "Update device online/offline status")
    public ResponseEntity<ApiResponse<DeviceResponse>> updateDeviceStatus(
            @Parameter(description = "Device ID string (e.g. FRIDGE-001)", example = "FRIDGE-001") @PathVariable String deviceId,
            @Parameter(description = "Online status") @RequestParam boolean online) {
        log.info("Updating device {} status to: {}", deviceId, online ? "online" : "offline");
        try {
            DeviceResponse device = deviceService.updateDeviceStatus(deviceId, online);
            return ResponseEntity.ok(ApiResponse.success(device, "Device status updated"));
        } catch (RuntimeException e) {
            log.error("Error updating device status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{deviceId}")
    @Operation(summary = "Deactivate device", description = "Deactivate a device (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deactivateDevice(
            @Parameter(description = "Device ID string (e.g. FRIDGE-001)", example = "FRIDGE-001") @PathVariable String deviceId) {
        log.info("Deactivating device: {}", deviceId);
        try {
            deviceService.deactivateDevice(deviceId);
            return ResponseEntity.ok(ApiResponse.success(null, "Device deactivated successfully"));
        } catch (RuntimeException e) {
            log.error("Error deactivating device: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 