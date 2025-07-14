package com.groceryautomation.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groceryautomation.controller.DeviceController;
import com.groceryautomation.dto.request.DeviceRegistrationRequest;
import com.groceryautomation.dto.response.DeviceResponse;
import com.groceryautomation.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterDeviceSuccessfully() throws Exception {
        // Arrange
        final DeviceRegistrationRequest request = DeviceRegistrationRequest.builder()
                .deviceId("FRIDGE-001")
                .name("Kitchen Smart Fridge")
                .userId(1L)
                .build();

        final DeviceResponse response = DeviceResponse.builder()
                .id(1L)
                .deviceId("FRIDGE-001")
                .name("Kitchen Smart Fridge")
                .userId(1L)
                .userEmail("john.doe@example.com")
                .active(true)
                .online(true)
                .lastSync(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .inventoryItemCount(0)
                .build();

        when(deviceService.registerDevice(any(DeviceRegistrationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/devices/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.deviceId").value("FRIDGE-001"))
                .andExpect(jsonPath("$.data.name").value("Kitchen Smart Fridge"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.online").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenDeviceRegistrationDataInvalid() throws Exception {
        // Arrange
        final DeviceRegistrationRequest request = DeviceRegistrationRequest.builder()
                .deviceId("") // Invalid - empty device ID
                .name("Kitchen Smart Fridge")
                .userId(null) // Invalid - null user ID
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/devices/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetDeviceByIdSuccessfully() throws Exception {
        // Arrange
        final String deviceId = "FRIDGE-001";
        final DeviceResponse response = DeviceResponse.builder()
                .id(1L)
                .deviceId(deviceId)
                .name("Kitchen Smart Fridge")
                .userId(1L)
                .userEmail("john.doe@example.com")
                .active(true)
                .online(true)
                .lastSync(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .inventoryItemCount(5)
                .build();

        when(deviceService.getDeviceById(deviceId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/devices/{deviceId}", deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.deviceId").value("FRIDGE-001"))
                .andExpect(jsonPath("$.data.inventoryItemCount").value(5));
    }

    @Test
    void shouldReturnNotFoundWhenDeviceDoesNotExist() throws Exception {
        // Arrange
        final String deviceId = "NON-EXISTENT";
        when(deviceService.getDeviceById(deviceId)).thenThrow(new RuntimeException("Device not found: " + deviceId));

        // Act & Assert
        mockMvc.perform(get("/api/devices/{deviceId}", deviceId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldGetUserDevicesSuccessfully() throws Exception {
        // Arrange
        final Long userId = 1L;
        final List<DeviceResponse> devices = Arrays.asList(
                DeviceResponse.builder()
                        .id(1L)
                        .deviceId("FRIDGE-001")
                        .name("Kitchen Smart Fridge")
                        .userId(userId)
                        .active(true)
                        .online(true)
                        .inventoryItemCount(5)
                        .build(),
                DeviceResponse.builder()
                        .id(2L)
                        .deviceId("PANTRY-001")
                        .name("Pantry Sensor")
                        .userId(userId)
                        .active(true)
                        .online(false)
                        .inventoryItemCount(3)
                        .build()
        );

        when(deviceService.getUserDevices(userId)).thenReturn(devices);

        // Act & Assert
        mockMvc.perform(get("/api/devices/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].deviceId").value("FRIDGE-001"))
                .andExpect(jsonPath("$.data[0].online").value(true))
                .andExpect(jsonPath("$.data[1].deviceId").value("PANTRY-001"))
                .andExpect(jsonPath("$.data[1].online").value(false));
    }

    @Test
    void shouldUpdateDeviceStatusSuccessfully() throws Exception {
        // Arrange
        final String deviceId = "FRIDGE-001";
        final boolean online = false;
        
        final DeviceResponse response = DeviceResponse.builder()
                .id(1L)
                .deviceId(deviceId)
                .name("Kitchen Smart Fridge")
                .userId(1L)
                .active(true)
                .online(online)
                .build();

        when(deviceService.updateDeviceStatus(deviceId, online)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/devices/{deviceId}/status", deviceId)
                .param("online", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.online").value(false));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidDeviceStatus() throws Exception {
        // Arrange
        final String deviceId = "FRIDGE-001";
        final String invalidStatus = "invalid_status";

        // Act & Assert
        mockMvc.perform(put("/api/devices/{deviceId}/status", deviceId)
                .param("status", invalidStatus))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeactivateDeviceSuccessfully() throws Exception {
        // Arrange
        final String deviceId = "FRIDGE-001";

        doNothing().when(deviceService).deactivateDevice(deviceId);

        // Act & Assert
        mockMvc.perform(delete("/api/devices/{deviceId}", deviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Device deactivated successfully"));
    }

    @Test
    void shouldReturnNotFoundWhenDeactivatingNonExistentDevice() throws Exception {
        // Arrange
        final String deviceId = "NON-EXISTENT";
        doThrow(new RuntimeException("Device not found: " + deviceId))
                .when(deviceService).deactivateDevice(deviceId);

        // Act & Assert
        mockMvc.perform(delete("/api/devices/{deviceId}", deviceId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldHandleConflictWhenDeviceIdAlreadyExists() throws Exception {
        // Arrange
        final DeviceRegistrationRequest request = DeviceRegistrationRequest.builder()
                .deviceId("FRIDGE-001")
                .name("Kitchen Smart Fridge")
                .userId(1L)
                .build();

        when(deviceService.registerDevice(any(DeviceRegistrationRequest.class)))
                .thenThrow(new RuntimeException("Device with ID FRIDGE-001 already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/devices/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoDevices() throws Exception {
        // Arrange
        final Long userId = 1L;
        when(deviceService.getUserDevices(userId)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/devices/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
} 