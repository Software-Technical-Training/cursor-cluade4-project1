package com.groceryautomation.unit.service;

import com.groceryautomation.dto.request.UserRegistrationRequest;
import com.groceryautomation.dto.response.UserResponse;
import com.groceryautomation.entity.User;
import com.groceryautomation.repository.OrderRepository;
import com.groceryautomation.repository.UserRepository;
import com.groceryautomation.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Test@123")
                .phone("+1234567890")
                .address("123 Main St, City, State 12345")
                .latitude(37.7749)
                .longitude(-122.4194)
                .build();

        final User savedUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Test@123")
                .phone("+1234567890")
                .address("123 Main St, City, State 12345")
                .latitude(37.7749)
                .longitude(-122.4194)
                .active(true)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        final UserResponse result = userService.registerUser(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getPhone()).isEqualTo("+1234567890");
        assertThat(result.getAddress()).isEqualTo("123 Main St, City, State 12345");
        assertThat(result.isActive()).isTrue();
        
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("John Doe")
                .email("existing@example.com")
                .password("Test@123")
                .phone("+1234567890")
                .address("123 Main St, City, State 12345")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered: existing@example.com");

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        final Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with ID: 999");

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Arrange
        final Long userId = 1L;
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("Updated Name")
                .email("john.doe@example.com")
                .password("Test@123")
                .phone("+1987654321")
                .address("456 Oak St, City, State 67890")
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        final User existingUser = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Test@123")
                .phone("+1234567890")
                .address("123 Main St, City, State 12345")
                .latitude(37.7749)
                .longitude(-122.4194)
                .active(true)
                .build();

        final User updatedUser = User.builder()
                .id(userId)
                .name("Updated Name")
                .email("john.doe@example.com")
                .password("Test@123")
                .phone("+1987654321")
                .address("456 Oak St, City, State 67890")
                .latitude(40.7128)
                .longitude(-74.0060)
                .active(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        final UserResponse result = userService.updateUser(userId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getPhone()).isEqualTo("+1987654321");
        assertThat(result.getAddress()).isEqualTo("456 Oak St, City, State 67890");

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingEmail() {
        // Arrange
        final Long userId = 1L;
        final UserRegistrationRequest request = UserRegistrationRequest.builder()
                .name("John Doe")
                .email("different@example.com")
                .password("Test@123")
                .phone("+1234567890")
                .address("123 Main St, City, State 12345")
                .build();

        final User existingUser = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Test@123")
                .phone("+1234567890")
                .address("123 Main St, City, State 12345")
                .active(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("different@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already in use: different@example.com");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("different@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // Arrange
        final Long userId = 1L;
        final User user = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Test@123")
                .phone("+1234567890")
                .address("123 Main St, City, State 12345")
                .active(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        final UserResponse result = userService.getUserById(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldGetAllUsersSuccessfully() {
        // Arrange
        final List<User> users = Arrays.asList(
                User.builder()
                        .id(1L)
                        .name("John Doe")
                        .email("john.doe@example.com")
                        .password("Test@123")
                        .phone("+1234567890")
                        .address("123 Main St")
                        .active(true)
                        .build(),
                User.builder()
                        .id(2L)
                        .name("Jane Smith")
                        .email("jane.smith@example.com")
                        .password("Test@456")
                        .phone("+1987654321")
                        .address("456 Oak St")
                        .active(true)
                        .build()
        );

        when(userRepository.findAll()).thenReturn(users);

        // Act
        final List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Smith");

        verify(userRepository).findAll();
    }

    @Test
    void shouldCheckEmailExistsCorrectly() {
        // Arrange
        final String existingEmail = "existing@example.com";
        final String nonExistingEmail = "nonexisting@example.com";

        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);
        when(userRepository.existsByEmail(nonExistingEmail)).thenReturn(false);

        // Act & Assert
        assertThat(userService.existsByEmail(existingEmail)).isTrue();
        assertThat(userService.existsByEmail(nonExistingEmail)).isFalse();

        verify(userRepository).existsByEmail(existingEmail);
        verify(userRepository).existsByEmail(nonExistingEmail);
    }
} 