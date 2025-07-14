package com.groceryautomation.unit.service;

import com.groceryautomation.dto.response.CurrentInventoryResponse;
import com.groceryautomation.dto.response.InventoryItemResponse;
import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.GroceryItem;
import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.entity.User;
import com.groceryautomation.enums.InventoryStatus;
import com.groceryautomation.repository.DeviceRepository;
import com.groceryautomation.repository.InventoryItemRepository;
import com.groceryautomation.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    void shouldGetCurrentInventorySuccessfully() {
        // Arrange
        final Long userId = 1L;
        final User user = createTestUser();
        final Device device = createTestDevice(user);
        final List<InventoryItem> inventoryItems = createTestInventoryItems(device);

        when(deviceRepository.findByUserIdAndActive(userId, true)).thenReturn(Arrays.asList(device));
        when(inventoryItemRepository.findByDeviceId(device.getId())).thenReturn(inventoryItems);

        // Act
        final CurrentInventoryResponse result = inventoryService.getCurrentInventory(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDeviceId()).isEqualTo("FRIDGE-001");
        assertThat(result.getDeviceName()).isEqualTo("Kitchen Smart Fridge");
        assertThat(result.isDeviceOnline()).isTrue();
        assertThat(result.getItems()).hasSize(3);
        assertThat(result.getTotalItems()).isEqualTo(3);
        assertThat(result.getLowStockItems()).isEqualTo(1); // One LOW item
        assertThat(result.getOutOfStockItems()).isEqualTo(1); // One OUT_OF_STOCK item

        verify(deviceRepository).findByUserIdAndActive(userId, true);
        verify(inventoryItemRepository).findByDeviceId(device.getId());
    }

    @Test
    void shouldReturnEmptyInventoryWhenNoActiveDevices() {
        // Arrange
        final Long userId = 1L;
        when(deviceRepository.findByUserIdAndActive(userId, true)).thenReturn(Collections.emptyList());

        // Act
        final CurrentInventoryResponse result = inventoryService.getCurrentInventory(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isDeviceOnline()).isFalse();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getLowStockItems()).isEqualTo(0);
        assertThat(result.getOutOfStockItems()).isEqualTo(0);

        verify(deviceRepository).findByUserIdAndActive(userId, true);
        verify(inventoryItemRepository, never()).findByDeviceId(any());
    }

    @Test
    void shouldGetInventoryAlertsSuccessfully() {
        // Arrange
        final Long userId = 1L;
        final List<InventoryStatus> alertStatuses = Arrays.asList(
                InventoryStatus.LOW, InventoryStatus.CRITICAL, InventoryStatus.OUT_OF_STOCK);
        final List<InventoryItem> alertItems = createAlertInventoryItems();

        when(inventoryItemRepository.findByUserIdAndStatusIn(userId, alertStatuses)).thenReturn(alertItems);

        // Act
        final List<InventoryItemResponse> result = inventoryService.getInventoryAlerts(userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo(InventoryStatus.LOW);
        assertThat(result.get(1).getStatus()).isEqualTo(InventoryStatus.OUT_OF_STOCK);

        verify(inventoryItemRepository).findByUserIdAndStatusIn(userId, alertStatuses);
    }

    @Test
    void shouldGetInventoryByStatusSuccessfully() {
        // Arrange
        final Long userId = 1L;
        final InventoryStatus status = InventoryStatus.LOW;
        final List<InventoryItem> lowStockItems = Arrays.asList(
                createInventoryItem("Milk", 0.3, 0.5, InventoryStatus.LOW)
        );

        when(inventoryItemRepository.findByUserIdAndStatusIn(userId, Arrays.asList(status)))
                .thenReturn(lowStockItems);

        // Act
        final List<InventoryItemResponse> result = inventoryService.getInventoryByStatus(userId, status);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(InventoryStatus.LOW);
        assertThat(result.get(0).getName()).isEqualTo("Milk");

        verify(inventoryItemRepository).findByUserIdAndStatusIn(userId, Arrays.asList(status));
    }

    @Test
    void shouldUpdateThresholdSuccessfully() {
        // Arrange
        final Long inventoryItemId = 1L;
        final Double newThreshold = 2.0;
        final InventoryItem item = createInventoryItem("Milk", 1.5, 1.0, InventoryStatus.SUFFICIENT);
        final InventoryItem updatedItem = createInventoryItem("Milk", 1.5, 2.0, InventoryStatus.LOW);

        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(updatedItem);

        // Act
        final InventoryItemResponse result = inventoryService.updateThreshold(inventoryItemId, newThreshold);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getThresholdQuantity()).isEqualTo(2.0);
        assertThat(result.getStatus()).isEqualTo(InventoryStatus.LOW);

        verify(inventoryItemRepository).findById(inventoryItemId);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
    }

    @Test
    void shouldThrowExceptionWhenInventoryItemNotFoundForThresholdUpdate() {
        // Arrange
        final Long inventoryItemId = 999L;
        final Double newThreshold = 2.0;
        when(inventoryItemRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.updateThreshold(inventoryItemId, newThreshold))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Inventory item not found: 999");

        verify(inventoryItemRepository).findById(inventoryItemId);
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    void shouldSyncInventorySuccessfully() {
        // Arrange
        final String deviceId = "FRIDGE-001";
        final Device device = Device.builder()
                .id(1L)
                .deviceId(deviceId)
                .name("Kitchen Smart Fridge")
                .online(true)
                .lastSync(LocalDateTime.now().minusHours(1))
                .build();

        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        // Act
        inventoryService.syncInventory(deviceId);

        // Assert
        verify(deviceRepository).findByDeviceId(deviceId);
        verify(deviceRepository).save(argThat(savedDevice -> 
            savedDevice.getLastSync().isAfter(LocalDateTime.now().minusMinutes(1))));
    }

    @Test
    void shouldThrowExceptionWhenDeviceNotFoundForSync() {
        // Arrange
        final String deviceId = "NON-EXISTENT";
        when(deviceRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.syncInventory(deviceId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Device not found: NON-EXISTENT");

        verify(deviceRepository).findByDeviceId(deviceId);
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void shouldCalculateCorrectStatusCounts() {
        // Arrange
        final Long userId = 1L;
        final User user = createTestUser();
        final Device device = createTestDevice(user);
        final List<InventoryItem> inventoryItems = Arrays.asList(
                createInventoryItem("Milk", 2.0, 1.0, InventoryStatus.SUFFICIENT),
                createInventoryItem("Eggs", 0.3, 0.5, InventoryStatus.LOW),
                createInventoryItem("Bread", 0.2, 1.0, InventoryStatus.CRITICAL),
                createInventoryItem("Butter", 0.0, 0.5, InventoryStatus.OUT_OF_STOCK)
        );

        when(deviceRepository.findByUserIdAndActive(userId, true)).thenReturn(Arrays.asList(device));
        when(inventoryItemRepository.findByDeviceId(device.getId())).thenReturn(inventoryItems);

        // Act
        final CurrentInventoryResponse result = inventoryService.getCurrentInventory(userId);

        // Assert
        assertThat(result.getTotalItems()).isEqualTo(4);
        assertThat(result.getLowStockItems()).isEqualTo(2); // LOW + CRITICAL
        assertThat(result.getOutOfStockItems()).isEqualTo(1); // OUT_OF_STOCK only

        verify(deviceRepository).findByUserIdAndActive(userId, true);
        verify(inventoryItemRepository).findByDeviceId(device.getId());
    }

    // Helper methods
    private User createTestUser() {
        return User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    private Device createTestDevice(User user) {
        return Device.builder()
                .id(1L)
                .deviceId("FRIDGE-001")
                .name("Kitchen Smart Fridge")
                .user(user)
                .online(true)
                .lastSync(LocalDateTime.now())
                .build();
    }

    private List<InventoryItem> createTestInventoryItems(Device device) {
        return Arrays.asList(
                createInventoryItem("Milk", 2.0, 1.0, InventoryStatus.SUFFICIENT),
                createInventoryItem("Eggs", 0.3, 0.5, InventoryStatus.LOW),
                createInventoryItem("Bread", 0.0, 1.0, InventoryStatus.OUT_OF_STOCK)
        );
    }

    private List<InventoryItem> createAlertInventoryItems() {
        return Arrays.asList(
                createInventoryItem("Eggs", 0.3, 0.5, InventoryStatus.LOW),
                createInventoryItem("Bread", 0.0, 1.0, InventoryStatus.OUT_OF_STOCK)
        );
    }

    private InventoryItem createInventoryItem(String name, Double quantity, Double threshold, InventoryStatus status) {
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
                .status(status)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
} 