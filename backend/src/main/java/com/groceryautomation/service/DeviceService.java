package com.groceryautomation.service;

import com.groceryautomation.dto.request.DeviceRegistrationRequest;
import com.groceryautomation.dto.response.DeviceResponse;
import com.groceryautomation.entity.Device;

import java.util.List;

public interface DeviceService {
    
    DeviceResponse registerDevice(DeviceRegistrationRequest request);
    
    List<DeviceResponse> getUserDevices(Long userId);
    
    DeviceResponse getDeviceById(String deviceId);
    
    Device getDeviceById(Long id);
    
    List<Device> getDevicesByUserId(Long userId);
    
    DeviceResponse updateDeviceStatus(String deviceId, boolean online);
    
    void deactivateDevice(String deviceId);
} 