package com.groceryautomation.service.impl;

import com.groceryautomation.dto.request.DeviceRegistrationRequest;
import com.groceryautomation.dto.response.DeviceResponse;
import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.User;
import com.groceryautomation.repository.DeviceRepository;
import com.groceryautomation.repository.UserRepository;
import com.groceryautomation.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeviceServiceImpl implements DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    
    @Override
    public DeviceResponse registerDevice(DeviceRegistrationRequest request) {
        // Check if device ID already exists
        if (deviceRepository.existsByDeviceId(request.getDeviceId())) {
            throw new RuntimeException("Device ID already exists: " + request.getDeviceId());
        }
        
        // Get user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));
        
        // Create device
        Device device = Device.builder()
                .deviceId(request.getDeviceId())
                .name(request.getName() != null ? request.getName() : "Smart Fridge Sensor")
                .user(user)
                .active(true)
                .online(true)
                .lastSync(LocalDateTime.now())
                .build();
        
        Device savedDevice = deviceRepository.save(device);
        log.info("Device registered successfully: {} for user: {}", savedDevice.getDeviceId(), user.getEmail());
        
        return mapToResponse(savedDevice);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeviceResponse> getUserDevices(Long userId) {
        List<Device> devices = deviceRepository.findByUserId(userId);
        return devices.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public DeviceResponse getDeviceById(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));
        return mapToResponse(device);
    }
    
    @Override
    public DeviceResponse updateDeviceStatus(String deviceId, boolean online) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));
        
        device.setOnline(online);
        if (online) {
            device.setLastSync(LocalDateTime.now());
        }
        
        Device updatedDevice = deviceRepository.save(device);
        log.info("Device {} status updated to: {}", deviceId, online ? "online" : "offline");
        
        return mapToResponse(updatedDevice);
    }
    
    @Override
    public void deactivateDevice(String deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));
        
        device.setActive(false);
        device.setOnline(false);
        deviceRepository.save(device);
        
        log.info("Device deactivated: {}", deviceId);
    }
    
    private DeviceResponse mapToResponse(Device device) {
        return DeviceResponse.builder()
                .id(device.getId())
                .deviceId(device.getDeviceId())
                .name(device.getName())
                .userId(device.getUser().getId())
                .userEmail(device.getUser().getEmail())
                .active(device.isActive())
                .online(device.isOnline())
                .lastSync(device.getLastSync())
                .createdAt(device.getCreatedAt())
                .inventoryItemCount(device.getInventoryItems() != null ? device.getInventoryItems().size() : 0)
                .build();
    }
} 