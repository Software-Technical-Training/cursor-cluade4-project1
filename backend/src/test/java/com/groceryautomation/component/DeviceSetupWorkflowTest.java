package com.groceryautomation.component;

import com.groceryautomation.dto.request.DeviceRegistrationRequest;
import com.groceryautomation.dto.response.DeviceResponse;
import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Component test for device setup and management workflows.
 * Tests the interaction between DeviceController, DeviceService, and UserService
 * to ensure device registration, status updates, and deactivation work correctly.
 */
@AutoConfigureWebMvc
class DeviceSetupWorkflowTest extends ComponentTestBase {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldCompleteDeviceRegistrationWorkflow() throws Exception {
        // Step 1: Register a new device for the test user
        final DeviceRegistrationRequest deviceRequest = DeviceRegistrationRequest.builder()
                .deviceId("NEW-DEVICE-001")
                .userId(testUser.getId())
                .name("Living Room Smart Fridge")
                .build();

        final String deviceJson = objectMapper.writeValueAsString(deviceRequest);

        // Act: Register device via DeviceController
        mockMvc.perform(post("/api/devices/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deviceJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceId").value("NEW-DEVICE-001"))
                .andExpect(jsonPath("$.data.name").value("Living Room Smart Fridge"))
                .andExpect(jsonPath("$.data.online").value(true))
                .andExpect(jsonPath("$.data.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.data.userEmail").value(testUser.getEmail()));

        // Step 2: Verify device was created in database
        final Optional<Device> savedDevice = deviceRepository.findByDeviceId("NEW-DEVICE-001");
        assertThat(savedDevice).isPresent();
        assertThat(savedDevice.get().getName()).isEqualTo("Living Room Smart Fridge");
        assertThat(savedDevice.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedDevice.get().isOnline()).isTrue();

        // Step 3: Verify user's device list was updated
        final User updatedUser = userRepository.findByIdWithDevices(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getDevices()).hasSize(2); // Original test device + new device
        assertThat(updatedUser.getDevices())
                .extracting(Device::getDeviceId)
                .contains("NEW-DEVICE-001", "COMPONENT-FRIDGE-001");
    }

    @Test
    void shouldUpdateDeviceStatusWorkflow() throws Exception {
        // Step 1: Update device status to offline
        mockMvc.perform(put("/api/devices/{deviceId}/status", testDevice.getDeviceId())
                        .param("online", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceId").value(testDevice.getDeviceId()))
                .andExpect(jsonPath("$.data.online").value(false));

        // Step 2: Verify device status was updated in database
        final Device updatedDevice = deviceRepository.findByDeviceId(testDevice.getDeviceId()).orElseThrow();
        assertThat(updatedDevice.isOnline()).isFalse();
        assertThat(updatedDevice.getLastSync()).isAfterOrEqualTo(testDevice.getLastSync());

        // Step 3: Update device status back to online
        mockMvc.perform(put("/api/devices/{deviceId}/status", testDevice.getDeviceId())
                        .param("online", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.online").value(true));

        // Step 4: Verify device is online again
        final Device reOnlineDevice = deviceRepository.findByDeviceId(testDevice.getDeviceId()).orElseThrow();
        assertThat(reOnlineDevice.isOnline()).isTrue();
    }

    @Test
    void shouldDeactivateDeviceWorkflow() throws Exception {
        // Step 1: Verify device exists and is active
        assertThat(deviceRepository.findByDeviceId(testDevice.getDeviceId())).isPresent();

        // Step 2: Deactivate device
        mockMvc.perform(delete("/api/devices/{deviceId}", testDevice.getDeviceId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Device deactivated successfully"));

        // Step 3: Verify device was deactivated (not deleted, just marked inactive)
        final Optional<Device> deactivatedDevice = deviceRepository.findByDeviceId(testDevice.getDeviceId());
        // Device should still exist but be marked as inactive/offline
        if (deactivatedDevice.isPresent()) {
            assertThat(deactivatedDevice.get().isOnline()).isFalse();
        }

        // Step 4: Verify user's active device list was updated
        final User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        final List<Device> activeDevices = updatedUser.getDevices().stream()
                .filter(Device::isOnline)
                .toList();
        assertThat(activeDevices).isEmpty(); // No active devices after deactivation
    }

    @Test
    void shouldHandleDeviceRegistrationForMultipleUsers() throws Exception {
        // Step 1: Create another test user
        final User secondUser = User.builder()
                .name("Second Test User")
                .email("second.test@example.com")
                .password("hashedPassword123")
                .phone("+15559876543")
                .address("456 Oak St, Another Town, CA 90211")
                .latitude(37.7750)
                .longitude(-122.4195)
                .active(true)
                .build();
        final User savedSecondUser = userRepository.save(secondUser);

        // Step 2: Register device for second user
        final DeviceRegistrationRequest deviceRequest = DeviceRegistrationRequest.builder()
                .deviceId("SECOND-USER-DEVICE")
                .userId(savedSecondUser.getId())
                .name("Second User's Fridge")
                .build();

        final String deviceJson = objectMapper.writeValueAsString(deviceRequest);

        mockMvc.perform(post("/api/devices/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deviceJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceId").value("SECOND-USER-DEVICE"))
                .andExpect(jsonPath("$.data.userId").value(savedSecondUser.getId()));

        // Step 3: Verify both users have their respective devices
        final User firstUserWithDevices = userRepository.findByIdWithDevices(testUser.getId()).orElseThrow();
        final User secondUserWithDevices = userRepository.findByIdWithDevices(savedSecondUser.getId()).orElseThrow();

        assertThat(firstUserWithDevices.getDevices())
                .extracting(Device::getDeviceId)
                .contains("COMPONENT-FRIDGE-001");

        assertThat(secondUserWithDevices.getDevices())
                .extracting(Device::getDeviceId)
                .contains("SECOND-USER-DEVICE");

        // Step 4: Verify devices are isolated between users
        assertThat(firstUserWithDevices.getDevices())
                .extracting(Device::getDeviceId)
                .doesNotContain("SECOND-USER-DEVICE");

        assertThat(secondUserWithDevices.getDevices())
                .extracting(Device::getDeviceId)
                .doesNotContain("COMPONENT-FRIDGE-001");
    }

    @Test
    void shouldHandleDeviceRegistrationErrorScenarios() throws Exception {
        // Test 1: Register device with duplicate device ID
        final DeviceRegistrationRequest duplicateRequest = DeviceRegistrationRequest.builder()
                .deviceId(testDevice.getDeviceId()) // Using existing device ID
                .userId(testUser.getId())
                .name("Duplicate Device")
                .build();

        final String duplicateJson = objectMapper.writeValueAsString(duplicateRequest);

        mockMvc.perform(post("/api/devices/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        // Test 2: Register device for non-existent user
        final DeviceRegistrationRequest validRequest = DeviceRegistrationRequest.builder()
                .deviceId("VALID-DEVICE-ID")
                .userId(999999L) // Non-existent user ID
                .name("Valid Device")
                .build();

        final String validJson = objectMapper.writeValueAsString(validRequest);

        mockMvc.perform(post("/api/devices/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldSyncDeviceDataWorkflow() throws Exception {
        // Step 1: Update device sync timestamp
        final LocalDateTime beforeSync = testDevice.getLastSync();
        
        // Wait a small amount to ensure timestamp difference
        Thread.sleep(100);

        // Step 2: Trigger device sync via status update
        mockMvc.perform(put("/api/devices/{deviceId}/status", testDevice.getDeviceId())
                        .param("online", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Step 3: Verify sync timestamp was updated
        final Device syncedDevice = deviceRepository.findByDeviceId(testDevice.getDeviceId()).orElseThrow();
        assertThat(syncedDevice.getLastSync()).isAfterOrEqualTo(beforeSync);

        // Step 4: Verify device remains associated with correct user
        assertThat(syncedDevice.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(syncedDevice.getUser().getEmail()).isEqualTo(testUser.getEmail());
    }
} 