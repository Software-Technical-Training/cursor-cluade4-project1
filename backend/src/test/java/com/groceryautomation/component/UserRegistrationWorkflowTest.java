package com.groceryautomation.component;

import com.groceryautomation.controller.UserController;
import com.groceryautomation.controller.DeviceController;
import com.groceryautomation.controller.StoreController;
import com.groceryautomation.dto.request.UserRegistrationRequest;
import com.groceryautomation.dto.request.DeviceRegistrationRequest;
import com.groceryautomation.dto.request.StoreSelectionRequest;
import com.groceryautomation.dto.response.ApiResponse;
import com.groceryautomation.dto.response.UserResponse;
import com.groceryautomation.dto.response.DeviceResponse;
import com.groceryautomation.dto.response.UserStoreResponse;
import com.groceryautomation.entity.User;
import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.Store;
import com.groceryautomation.entity.UserStore;
import com.groceryautomation.service.UserService;
import com.groceryautomation.service.DeviceService;
import com.groceryautomation.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Component test for the complete user registration workflow.
 * Tests the interaction between UserController, DeviceController, StoreController,
 * and their respective services to ensure the complete registration process works correctly.
 */
@AutoConfigureWebMvc
class UserRegistrationWorkflowTest extends ComponentTestBase {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserController userController;

    @Autowired
    private DeviceController deviceController;

    @Autowired
    private StoreController storeController;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private StoreService storeService;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldCompleteFullUserRegistrationWorkflow() throws Exception {
        // Arrange
        UserRegistrationRequest registrationRequest = UserRegistrationRequest.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .password("SecurePass123!")
                .phone("+15551234567")
                .address("456 Oak Street, San Francisco, CA 94102")
                .latitude(37.7849)
                .longitude(-122.4094)
                .build();

        // Act & Assert - Step 1: User Registration
        String registrationJson = objectMapper.writeValueAsString(registrationRequest);
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Jane Smith"))
                .andExpect(jsonPath("$.data.email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.data.phone").value("+15551234567"));

        // Verify user was created in database
        User createdUser = userRepository.findByEmail("jane.smith@example.com").orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("Jane Smith");
        assertThat(createdUser.isActive()).isTrue();

        // Act & Assert - Step 2: Device Registration
        DeviceRegistrationRequest deviceRequest = DeviceRegistrationRequest.builder()
                .deviceId("FRIDGE-NEW-001")
                .userId(createdUser.getId())
                .name("Jane's Smart Fridge")
                .build();

        String deviceJson = objectMapper.writeValueAsString(deviceRequest);
        
        mockMvc.perform(post("/api/devices/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceId").value("FRIDGE-NEW-001"))
                .andExpect(jsonPath("$.data.name").value("Jane's Smart Fridge"))
                .andExpect(jsonPath("$.data.userId").value(createdUser.getId()));

        // Verify device was created and linked to user
        Device createdDevice = deviceRepository.findByDeviceId("FRIDGE-NEW-001").orElse(null);
        assertThat(createdDevice).isNotNull();
        assertThat(createdDevice.getUser().getId()).isEqualTo(createdUser.getId());
        assertThat(createdDevice.isActive()).isTrue();

        // Act & Assert - Step 3: Store Selection (Primary Store)
        StoreSelectionRequest storeRequest = StoreSelectionRequest.builder()
                .name("Jane's Local Market")
                .address("789 Market Street, San Francisco, CA 94102")
                .latitude(37.7949)
                .longitude(-122.3994)
                .phone("+15559876543")
                .googlePlaceId("ChIJTestJanesPrimary")
                .priority(1)
                .maxDeliveryFee(8.0)
                .maxDistanceMiles(10.0)
                .build();

        String storeJson = objectMapper.writeValueAsString(storeRequest);
        
        mockMvc.perform(post("/api/stores/user/{userId}/select-store", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(storeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.store.name").value("Jane's Local Market"))
                .andExpect(jsonPath("$.data.priority").value(1));

        // Verify store was created and linked to user
        Store createdStore = storeRepository.findByGooglePlaceId("ChIJTestJanesPrimary").orElse(null);
        assertThat(createdStore).isNotNull();
        assertThat(createdStore.getName()).isEqualTo("Jane's Local Market");

        UserStore userStore = userStoreRepository.findByUserIdAndStoreId(createdUser.getId(), createdStore.getId()).orElse(null);
        assertThat(userStore).isNotNull();
        assertThat(userStore.getPriority()).isEqualTo(1);
        assertThat(userStore.isPrimary()).isTrue();

        // Act & Assert - Step 4: Backup Store Selection
        StoreSelectionRequest backupStoreRequest = StoreSelectionRequest.builder()
                .name("Jane's Backup Store")
                .address("321 Pine Street, San Francisco, CA 94102")
                .latitude(37.7749)
                .longitude(-122.4194)
                .phone("+15555551234")
                .googlePlaceId("ChIJTestJanesBackup")
                .priority(2)
                .maxDeliveryFee(10.0)
                .maxDistanceMiles(15.0)
                .build();

        String backupStoreJson = objectMapper.writeValueAsString(backupStoreRequest);
        
        mockMvc.perform(post("/api/stores/user/{userId}/select-store", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(backupStoreJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.store.name").value("Jane's Backup Store"))
                .andExpect(jsonPath("$.data.priority").value(2));

        // Verify backup store was created and linked to user
        Store backupStore = storeRepository.findByGooglePlaceId("ChIJTestJanesBackup").orElse(null);
        assertThat(backupStore).isNotNull();
        assertThat(backupStore.getName()).isEqualTo("Jane's Backup Store");

        UserStore backupUserStore = userStoreRepository.findByUserIdAndStoreId(createdUser.getId(), backupStore.getId()).orElse(null);
        assertThat(backupUserStore).isNotNull();
        assertThat(backupUserStore.getPriority()).isEqualTo(2);
        assertThat(backupUserStore.isPrimary()).isFalse();

        // Act & Assert - Step 5: Verify Complete Registration State
        mockMvc.perform(get("/api/users/{id}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Jane Smith"))
                .andExpect(jsonPath("$.data.email").value("jane.smith@example.com"));

        mockMvc.perform(get("/api/devices/user/{userId}", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].deviceId").value("FRIDGE-NEW-001"));

        mockMvc.perform(get("/api/stores/user/{userId}/stores", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].priority").value(1))
                .andExpect(jsonPath("$.data[1].priority").value(2));

        // Verify service layer state
        UserResponse userResponse = userService.getUserById(createdUser.getId());
        assertThat(userResponse.getName()).isEqualTo("Jane Smith");
        assertThat(userResponse.isActive()).isTrue();

        List<DeviceResponse> devices = deviceService.getUserDevices(createdUser.getId());
        assertThat(devices).hasSize(1);
        assertThat(devices.get(0).getDeviceId()).isEqualTo("FRIDGE-NEW-001");

        List<UserStoreResponse> stores = storeService.getUserStores(createdUser.getId());
        assertThat(stores).hasSize(2);
        assertThat(stores.get(0).getPriority()).isEqualTo(1);
        assertThat(stores.get(1).getPriority()).isEqualTo(2);
    }

    @Test
    void shouldHandleRegistrationWithDuplicateEmail() throws Exception {
        // Arrange - Create a user first
        UserRegistrationRequest firstRequest = UserRegistrationRequest.builder()
                .name("John Doe")
                .email("duplicate@example.com")
                .password("FirstPass123!")
                .phone("+15551111111")
                .address("123 First Street")
                .build();

        String firstJson = objectMapper.writeValueAsString(firstRequest);
        
        // Act - Register first user successfully
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        // Arrange - Try to register with same email
        UserRegistrationRequest duplicateRequest = UserRegistrationRequest.builder()
                .name("Jane Doe")
                .email("duplicate@example.com")
                .password("SecondPass123!")
                .phone("+15552222222")
                .address("456 Second Street")
                .build();

        String duplicateJson = objectMapper.writeValueAsString(duplicateRequest);

        // Act & Assert - Second registration should fail
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        // Verify only one user exists
        List<User> users = userRepository.findAll();
        long duplicateEmailCount = users.stream()
                .filter(user -> "duplicate@example.com".equals(user.getEmail()))
                .count();
        assertThat(duplicateEmailCount).isEqualTo(1);
    }

    @Test
    void shouldHandleDeviceRegistrationWithInvalidUser() throws Exception {
        // Arrange
        DeviceRegistrationRequest deviceRequest = DeviceRegistrationRequest.builder()
                .deviceId("FRIDGE-INVALID-001")
                .userId(99999L) // Non-existent user ID
                .name("Invalid User's Fridge")
                .build();

        String deviceJson = objectMapper.writeValueAsString(deviceRequest);

        // Act & Assert
        mockMvc.perform(post("/api/devices/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        // Verify device was not created
        assertThat(deviceRepository.findByDeviceId("FRIDGE-INVALID-001")).isEmpty();
    }

    @Test
    void shouldHandleStoreSelectionWithInvalidUser() throws Exception {
        // Arrange
        StoreSelectionRequest storeRequest = StoreSelectionRequest.builder()
                .name("Invalid User's Store")
                .address("999 Nowhere Street")
                .latitude(37.7749)
                .longitude(-122.4194)
                .googlePlaceId("ChIJInvalidUser")
                .build();

        String storeJson = objectMapper.writeValueAsString(storeRequest);

        // Act & Assert
        mockMvc.perform(post("/api/stores/user/{userId}/select-store", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(storeJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        // Verify store was not created
        assertThat(storeRepository.findByGooglePlaceId("ChIJInvalidUser")).isEmpty();
    }

    @Test
    void shouldHandleValidationErrorsProperly() throws Exception {
        // Arrange - Invalid registration request (missing required fields)
        UserRegistrationRequest invalidRequest = UserRegistrationRequest.builder()
                .name("") // Invalid - too short
                .email("invalid-email") // Invalid format
                .password("weak") // Invalid - too weak
                .phone("123") // Invalid format
                .address("") // Invalid - empty
                .build();

        String invalidJson = objectMapper.writeValueAsString(invalidRequest);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        // Verify no user was created
        assertThat(userRepository.findByEmail("invalid-email")).isEmpty();
    }

    @Test
    void shouldMaintainTransactionIntegrityOnFailure() throws Exception {
        // This test would verify that if any step in the registration process fails,
        // the entire transaction is rolled back properly.
        // For this POC, we'll simulate a basic scenario where device registration fails
        // after user registration succeeds, but since they're separate requests,
        // the user should still exist.

        // Arrange
        UserRegistrationRequest userRequest = UserRegistrationRequest.builder()
                .name("Transaction Test User")
                .email("transaction@example.com")
                .password("TransactionPass123!")
                .phone("+15551234567")
                .address("123 Transaction Street")
                .build();

        String userJson = objectMapper.writeValueAsString(userRequest);

        // Act - Register user successfully
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));

        User createdUser = userRepository.findByEmail("transaction@example.com").orElse(null);
        assertThat(createdUser).isNotNull();

        // Act - Try to register device with duplicate device ID
        DeviceRegistrationRequest deviceRequest = DeviceRegistrationRequest.builder()
                .deviceId("FRIDGE-001") // This should already exist from base test setup
                .userId(createdUser.getId())
                .name("Duplicate Device")
                .build();

        String deviceJson = objectMapper.writeValueAsString(deviceRequest);

        // Assert - Device registration should fail
        mockMvc.perform(post("/api/devices/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deviceJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        // Verify user still exists (separate transaction)
        User userAfterFailure = userRepository.findByEmail("transaction@example.com").orElse(null);
        assertThat(userAfterFailure).isNotNull();
        assertThat(userAfterFailure.getName()).isEqualTo("Transaction Test User");

        // Verify no duplicate device was created
        List<Device> devicesWithId = deviceRepository.findAll().stream()
                .filter(device -> "FRIDGE-001".equals(device.getDeviceId()))
                .toList();
        assertThat(devicesWithId).hasSize(1); // Only the original from test setup
    }
} 