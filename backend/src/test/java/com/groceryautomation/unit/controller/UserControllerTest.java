package com.groceryautomation.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groceryautomation.controller.UserController;
import com.groceryautomation.dto.request.UserRegistrationRequest;
import com.groceryautomation.dto.response.UserResponse;
import com.groceryautomation.service.UserService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // Arrange
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Password123!")
                .phone("+15551234567")
                .address("123 Main St")
                .latitude(37.7749)
                .longitude(-122.4194)
                .build();

        final UserResponse response = UserResponse.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("(555) 123-4567")
                .address("123 Main St")
                .latitude(37.7749)
                .longitude(-122.4194)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deviceCount(0)
                .activeOrderCount(0)
                .build();

        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenRegistrationDataInvalid() throws Exception {
        // Arrange
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("") // Invalid - empty name
                .email("invalid-email") // Invalid email format
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        // Arrange
        final Long userId = 1L;
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .password("NewPassword123!")
                .phone("+15559876543")
                .address("456 Oak St")
                .latitude(37.7750)
                .longitude(-122.4195)
                .build();

        final UserResponse response = UserResponse.builder()
                .id(userId)
                .name("John Updated")
                .email("john.updated@example.com")
                .phone("(555) 987-6543")
                .address("456 Oak St")
                .latitude(37.7750)
                .longitude(-122.4195)
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .deviceCount(1)
                .activeOrderCount(0)
                .build();

        when(userService.updateUser(eq(userId), any(UserRegistrationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.name").value("John Updated"))
                .andExpect(jsonPath("$.data.email").value("john.updated@example.com"));
    }

    @Test
    void shouldGetUserByIdSuccessfully() throws Exception {
        // Arrange
        final Long userId = 1L;
        final UserResponse response = UserResponse.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("(555) 123-4567")
                .address("123 Main St")
                .latitude(37.7749)
                .longitude(-122.4194)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deviceCount(2)
                .activeOrderCount(1)
                .build();

        when(userService.getUserById(userId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.deviceCount").value(2))
                .andExpect(jsonPath("$.data.activeOrderCount").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Arrange
        final Long userId = 999L;
        when(userService.getUserById(userId)).thenThrow(new RuntimeException("User not found: " + userId));

        // Act & Assert
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldGetAllUsersSuccessfully() throws Exception {
        // Arrange
        final List<UserResponse> users = Arrays.asList(
                UserResponse.builder()
                        .id(1L)
                        .name("John Doe")
                        .email("john.doe@example.com")
                        .active(true)
                        .deviceCount(1)
                        .activeOrderCount(0)
                        .build(),
                UserResponse.builder()
                        .id(2L)
                        .name("Jane Smith")
                        .email("jane.smith@example.com")
                        .active(true)
                        .deviceCount(2)
                        .activeOrderCount(1)
                        .build()
        );

        when(userService.getAllUsers()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Jane Smith"));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void shouldHandleInternalServerErrorGracefully() throws Exception {
        // Arrange
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Password123!")
                .phone("+15551234567")
                .address("123 Main St")
                .build();

        when(userService.registerUser(any(UserRegistrationRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldValidateRequiredFieldsInRegistration() throws Exception {
        // Arrange
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .build(); // Empty request - should trigger validation

        // Act & Assert
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
} 