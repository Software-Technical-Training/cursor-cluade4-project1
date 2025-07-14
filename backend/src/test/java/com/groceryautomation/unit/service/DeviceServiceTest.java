package com.groceryautomation.unit.service;

import com.groceryautomation.dto.request.DeviceRegistrationRequest;
import com.groceryautomation.dto.response.DeviceResponse;
import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.User;
import com.groceryautomation.repository.DeviceRepository;
import com.groceryautomation.repository.UserRepository;
import com.groceryautomation.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Test
    void shouldRegisterDeviceSuccessfully() {
        // Arrange
        final DeviceRegistrationRequest request = DeviceRegistrationRequest.builder()
                .deviceId("FRIDGE-001")
                .userId(1L)
                .name("Kitchen Smart Fridge")
                .build();

        final User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        final Device savedDevice = Device.builder()
                .id(1L)
                .deviceId("FRIDGE-001")
                .name("Kitchen Smart Fridge")
                .user(user)
                .active(true)
                .online(true)
                .lastSync(LocalDateTime.now())
                .build();

        when(deviceRepository.existsByDeviceId(request.getDeviceId())).thenReturn(false);
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(deviceRepository.save(any(Device.class))).thenReturn(savedDevice);

        // Act
        final DeviceResponse result = deviceService.registerDevice(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDeviceId()).isEqualTo("FRIDGE-001");
        assertThat(result.getName()).isEqualTo("Kitchen Smart Fridge");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUserEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.isActive()).isTrue();
        assertThat(result.isOnline()).isTrue();

        verify(deviceRepository).existsByDeviceId(request.getDeviceId());
        verify(userRepository).findById(request.getUserId());
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void shouldThrowExceptionWhenDeviceIdAlreadyExists() {
        // Arrange
        final DeviceRegistrationRequest request = DeviceRegistrationRequest.builder()
                .deviceId("EXISTING-DEVICE")
                .userId(1L)
                .name("Kitchen Smart Fridge")
                .build();

        when(deviceRepository.existsByDeviceId(request.getDeviceId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> deviceService.registerDevice(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Device ID already exists: EXISTING-DEVICE");

        verify(deviceRepository).existsByDeviceId(request.getDeviceId());
        verify(userRepository, never()).findById(any());
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForDevice() {
        // Arrange
        final DeviceRegistrationRequest request = DeviceRegistrationRequest.builder()
                .deviceId("FRIDGE-001")
                .userId(999L)
                .name("Kitchen Smart Fridge")
                .build();

        when(deviceRepository.existsByDeviceId(request.getDeviceId())).thenReturn(false);
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deviceService.registerDevice(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found: 999");

        verify(deviceRepository).existsByDeviceId(request.getDeviceId());
        verify(userRepository).findById(request.getUserId());
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void shouldGetUserDevicesSuccessfully() {
        // Arrange
        final Long userId = 1L;
        final User user = User.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        final List<Device> devices = Arrays.asList(
                Device.builder()
                        .id(1L)
                        .deviceId("FRIDGE-001")
                        .name("Kitchen Smart Fridge")
                        .user(user)
                        .active(true)
                        .online(true)
                        .build(),
                Device.builder()
                        .id(2L)
                        .deviceId("PANTRY-001")
                        .name("Pantry Sensor")
                        .user(user)
                        .active(true)
                        .online(false)
                        .build()
        );

        when(deviceRepository.findByUserId(userId)).thenReturn(devices);

        // Act
        final List<DeviceResponse> result = deviceService.getUserDevices(userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDeviceId()).isEqualTo("FRIDGE-001");
        assertThat(result.get(0).getName()).isEqualTo("Kitchen Smart Fridge");
        assertThat(result.get(0).isOnline()).isTrue();
        assertThat(result.get(1).getDeviceId()).isEqualTo("PANTRY-001");
        assertThat(result.get(1).getName()).isEqualTo("Pantry Sensor");
        assertThat(result.get(1).isOnline()).isFalse();

        verify(deviceRepository).findByUserId(userId);
    }

    @Test
    void shouldGetDeviceByIdSuccessfully() {
        // Arrange
        final String deviceId = "FRIDGE-001";
        final User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        final Device device = Device.builder()
                .id(1L)
                .deviceId(deviceId)
                .name("Kitchen Smart Fridge")
                .user(user)
                .active(true)
                .online(true)
                .lastSync(LocalDateTime.now())
                .build();

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));

        // Act
        final DeviceResponse result = deviceService.getDeviceById(deviceId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDeviceId()).isEqualTo(deviceId);
        assertThat(result.getName()).isEqualTo("Kitchen Smart Fridge");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUserEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.isActive()).isTrue();
        assertThat(result.isOnline()).isTrue();

        verify(deviceRepository).findByDeviceId(deviceId);
    }

    @Test
    void shouldUpdateDeviceStatusSuccessfully() {
        // Arrange
        final String deviceId = "FRIDGE-001";
        final boolean online = false;
        final User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        final Device device = Device.builder()
                .id(1L)
                .deviceId(deviceId)
                .name("Kitchen Smart Fridge")
                .user(user)
                .active(true)
                .online(true)
                .lastSync(LocalDateTime.now().minusHours(1))
                .build();

        final Device updatedDevice = Device.builder()
                .id(1L)
                .deviceId(deviceId)
                .name("Kitchen Smart Fridge")
                .user(user)
                .active(true)
                .online(false)
                .lastSync(LocalDateTime.now().minusHours(1))
                .build();

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(updatedDevice);

        // Act
        final DeviceResponse result = deviceService.updateDeviceStatus(deviceId, online);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isOnline()).isFalse();
        assertThat(result.getDeviceId()).isEqualTo(deviceId);

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void shouldDeactivateDeviceSuccessfully() {
        // Arrange
        final String deviceId = "FRIDGE-001";
        final User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        final Device device = Device.builder()
                .id(1L)
                .deviceId(deviceId)
                .name("Kitchen Smart Fridge")
                .user(user)
                .active(true)
                .online(true)
                .build();

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));

        // Act
        deviceService.deactivateDevice(deviceId);

        // Assert
        verify(deviceRepository).findByDeviceId(deviceId);
        verify(deviceRepository).save(argThat(savedDevice -> 
            !savedDevice.isActive() && !savedDevice.isOnline()));
    }

    @Test
    void shouldThrowExceptionWhenDeviceNotFound() {
        // Arrange
        final String deviceId = "NON-EXISTENT";
        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> deviceService.getDeviceById(deviceId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Device not found: NON-EXISTENT");

        verify(deviceRepository).findByDeviceId(deviceId);
    }

    @Test
    void shouldUseDefaultNameWhenNotProvided() {
        // Arrange
        final DeviceRegistrationRequest request = DeviceRegistrationRequest.builder()
                .deviceId("FRIDGE-002")
                .userId(1L)
                .build(); // No name provided

        final User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        final Device savedDevice = Device.builder()
                .id(1L)
                .deviceId("FRIDGE-002")
                .name("Smart Fridge Sensor") // Default name
                .user(user)
                .active(true)
                .online(true)
                .build();

        when(deviceRepository.existsByDeviceId(request.getDeviceId())).thenReturn(false);
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));
        when(deviceRepository.save(any(Device.class))).thenReturn(savedDevice);

        // Act
        final DeviceResponse result = deviceService.registerDevice(request);

        // Assert
        assertThat(result.getName()).isEqualTo("Smart Fridge Sensor");

        verify(deviceRepository).save(argThat(device -> 
            device.getName().equals("Smart Fridge Sensor")));
    }
} 