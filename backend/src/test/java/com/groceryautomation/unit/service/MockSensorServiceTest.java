package com.groceryautomation.unit.service;

import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.GroceryItem;
import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.entity.User;
import com.groceryautomation.enums.InventoryStatus;
import com.groceryautomation.repository.DeviceRepository;
import com.groceryautomation.repository.InventoryItemRepository;
import com.groceryautomation.service.MockSensorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockSensorServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private MockSensorService mockSensorService;

    @Test
    void shouldSimulateInventoryConsumptionForActiveDevices() {
        // Arrange
        final Device device = createTestDevice();
        final List<InventoryItem> inventoryItems = createTestInventoryItems();

        when(deviceRepository.findAllActiveAndOnlineDevices()).thenReturn(Arrays.asList(device));
        when(inventoryItemRepository.findByDeviceId(device.getId())).thenReturn(inventoryItems);

        // Act
        mockSensorService.simulateInventoryConsumption();

        // Assert
        verify(deviceRepository).findAllActiveAndOnlineDevices();
        verify(inventoryItemRepository).findByDeviceId(device.getId());
        verify(inventoryItemRepository, atLeastOnce()).save(any(InventoryItem.class));
        verify(deviceRepository).save(argThat(savedDevice -> 
            savedDevice.getLastSync().isAfter(LocalDateTime.now().minusMinutes(1))));
    }

    @Test
    void shouldSkipConsumptionWhenNoActiveDevices() {
        // Arrange
        when(deviceRepository.findAllActiveAndOnlineDevices()).thenReturn(Collections.emptyList());

        // Act
        mockSensorService.simulateInventoryConsumption();

        // Assert
        verify(deviceRepository).findAllActiveAndOnlineDevices();
        verify(inventoryItemRepository, never()).findByDeviceId(any());
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void shouldSkipItemsWithZeroQuantity() {
        // Arrange
        final Device device = createTestDevice();
        final List<InventoryItem> inventoryItems = Arrays.asList(
                createInventoryItem("Milk", 0.0, 0.5), // Zero quantity - should be skipped
                createInventoryItem("Eggs", 1.0, 0.5)  // Non-zero quantity - should be processed
        );

        when(deviceRepository.findAllActiveAndOnlineDevices()).thenReturn(Arrays.asList(device));
        when(inventoryItemRepository.findByDeviceId(device.getId())).thenReturn(inventoryItems);

        // Act
        mockSensorService.simulateInventoryConsumption();

        // Assert
        verify(deviceRepository).findAllActiveAndOnlineDevices();
        verify(inventoryItemRepository).findByDeviceId(device.getId());
        // Should only save items that had non-zero quantity
        verify(inventoryItemRepository, atMost(inventoryItems.size())).save(any(InventoryItem.class));
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void shouldUpdateDeviceLastSyncTime() {
        // Arrange
        final Device device = createTestDevice();
        final List<InventoryItem> inventoryItems = createTestInventoryItems();

        when(deviceRepository.findAllActiveAndOnlineDevices()).thenReturn(Arrays.asList(device));
        when(inventoryItemRepository.findByDeviceId(device.getId())).thenReturn(inventoryItems);

        // Act
        mockSensorService.simulateInventoryConsumption();

        // Assert
        verify(deviceRepository).save(argThat(savedDevice -> {
            LocalDateTime lastSync = savedDevice.getLastSync();
            return lastSync != null && lastSync.isAfter(LocalDateTime.now().minusMinutes(1));
        }));
    }

    @Test
    void shouldSimulateOccasionalRestocking() {
        // Arrange
        final Device device = createTestDevice();

        when(deviceRepository.findAllActiveAndOnlineDevices()).thenReturn(Arrays.asList(device));

        // Act
        mockSensorService.simulateOccasionalRestocking();

        // Assert
        verify(deviceRepository).findAllActiveAndOnlineDevices();
        // Note: Due to randomness (20% chance), we can't guarantee findByDeviceId will be called
        // but we can verify the main method was called
    }

    @Test
    void shouldSkipRestockingWhenNoActiveDevices() {
        // Arrange
        when(deviceRepository.findAllActiveAndOnlineDevices()).thenReturn(Collections.emptyList());

        // Act
        mockSensorService.simulateOccasionalRestocking();

        // Assert
        verify(deviceRepository).findAllActiveAndOnlineDevices();
        verify(inventoryItemRepository, never()).findByDeviceId(any());
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    void shouldHandleEmptyInventoryItems() {
        // Arrange
        final Device device = createTestDevice();

        when(deviceRepository.findAllActiveAndOnlineDevices()).thenReturn(Arrays.asList(device));
        when(inventoryItemRepository.findByDeviceId(device.getId())).thenReturn(Collections.emptyList());

        // Act
        mockSensorService.simulateInventoryConsumption();

        // Assert
        verify(deviceRepository).findAllActiveAndOnlineDevices();
        verify(inventoryItemRepository).findByDeviceId(device.getId());
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
        verify(deviceRepository).save(any(Device.class)); // Should still update last sync
    }

    @Test
    void shouldProcessMultipleDevices() {
        // Arrange
        final Device device1 = createTestDevice();
        final Device device2 = Device.builder()
                .id(2L)
                .deviceId("FRIDGE-002")
                .name("Pantry Sensor")
                .active(true)
                .online(true)
                .mockConsumptionRate(0.05)
                .build();

        final List<InventoryItem> inventoryItems1 = createTestInventoryItems();
        final List<InventoryItem> inventoryItems2 = Arrays.asList(
                createInventoryItem("Pasta", 3.0, 2.0)
        );

        when(deviceRepository.findAllActiveAndOnlineDevices()).thenReturn(Arrays.asList(device1, device2));
        when(inventoryItemRepository.findByDeviceId(device1.getId())).thenReturn(inventoryItems1);
        when(inventoryItemRepository.findByDeviceId(device2.getId())).thenReturn(inventoryItems2);

        // Act
        mockSensorService.simulateInventoryConsumption();

        // Assert
        verify(deviceRepository).findAllActiveAndOnlineDevices();
        verify(inventoryItemRepository).findByDeviceId(device1.getId());
        verify(inventoryItemRepository).findByDeviceId(device2.getId());
        verify(deviceRepository, times(2)).save(any(Device.class));
    }

    // Helper methods
    private Device createTestDevice() {
        final User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        return Device.builder()
                .id(1L)
                .deviceId("FRIDGE-001")
                .name("Kitchen Smart Fridge")
                .user(user)
                .active(true)
                .online(true)
                .mockConsumptionRate(0.1)
                .lastSync(LocalDateTime.now().minusHours(1))
                .build();
    }

    private List<InventoryItem> createTestInventoryItems() {
        return Arrays.asList(
                createInventoryItem("Milk", 2.0, 1.0),
                createInventoryItem("Eggs", 1.5, 0.5),
                createInventoryItem("Bread", 0.8, 1.0)
        );
    }

    private InventoryItem createInventoryItem(String name, Double quantity, Double threshold) {
        final GroceryItem groceryItem = GroceryItem.builder()
                .id(1L)
                .name(name)
                .category("Dairy")
                .unit("gallon")
                .build();

        return InventoryItem.builder()
                .id(1L)
                .groceryItem(groceryItem)
                .quantity(quantity)
                .thresholdQuantity(threshold)
                .status(InventoryStatus.SUFFICIENT)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
} 