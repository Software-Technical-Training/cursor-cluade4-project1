package com.groceryautomation.unit.service;

import com.groceryautomation.dto.request.StoreSelectionRequest;
import com.groceryautomation.dto.request.UserStoreRequest;
import com.groceryautomation.dto.response.StoreResponse;
import com.groceryautomation.dto.response.UserStoreResponse;
import com.groceryautomation.entity.Store;
import com.groceryautomation.entity.User;
import com.groceryautomation.entity.UserStore;
import com.groceryautomation.repository.StoreRepository;
import com.groceryautomation.repository.UserRepository;
import com.groceryautomation.repository.UserStoreRepository;
import com.groceryautomation.service.impl.StoreServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStoreRepository userStoreRepository;

    @InjectMocks
    private StoreServiceImpl storeService;

    @Test
    void shouldFindNearbyStoresSuccessfully() {
        // Arrange
        final Double latitude = 37.7749;
        final Double longitude = -122.4194;
        final Double radius = 5.0;
        final Integer limit = 5;

        final List<Store> stores = Arrays.asList(
                createTestStore(1L, "Fresh Mart", 37.7750, -122.4195),
                createTestStore(2L, "QuickShop", 37.7748, -122.4193)
        );

        when(storeRepository.findNearbyStores(latitude, longitude, radius, limit)).thenReturn(stores);

        // Act
        final List<StoreResponse> result = storeService.findNearbyStores(latitude, longitude, radius, limit);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Fresh Mart");
        assertThat(result.get(1).getName()).isEqualTo("QuickShop");
        assertThat(result.get(0).getDistanceInMiles()).isNotNull();

        verify(storeRepository).findNearbyStores(latitude, longitude, radius, limit);
    }

    @Test
    void shouldGetAllActiveStoresSuccessfully() {
        // Arrange
        final List<Store> stores = Arrays.asList(
                createTestStore(1L, "Fresh Mart", 37.7749, -122.4194),
                createTestStore(2L, "QuickShop", 37.7748, -122.4193)
        );

        when(storeRepository.findByActiveTrue()).thenReturn(stores);

        // Act
        final List<StoreResponse> result = storeService.getAllActiveStores();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Fresh Mart");
        assertThat(result.get(1).getName()).isEqualTo("QuickShop");

        verify(storeRepository).findByActiveTrue();
    }

    @Test
    void shouldGetStoreByIdSuccessfully() {
        // Arrange
        final Long storeId = 1L;
        final Store store = createTestStore(storeId, "Fresh Mart", 37.7749, -122.4194);

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // Act
        final StoreResponse result = storeService.getStoreById(storeId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Fresh Mart");
        assertThat(result.getLatitude()).isEqualTo(37.7749);
        assertThat(result.getLongitude()).isEqualTo(-122.4194);

        verify(storeRepository).findById(storeId);
    }

    @Test
    void shouldThrowExceptionWhenStoreNotFound() {
        // Arrange
        final Long storeId = 999L;
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> storeService.getStoreById(storeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Store not found: 999");

        verify(storeRepository).findById(storeId);
    }

    @Test
    void shouldSelectStoreForUserSuccessfully() {
        // Arrange
        final Long userId = 1L;
        final StoreSelectionRequest request = StoreSelectionRequest.builder()
                .name("Fresh Mart")
                .address("123 Main St")
                .latitude(37.7749)
                .longitude(-122.4194)
                .phone("(555) 123-4567")
                .googlePlaceId("place123")
                .priority(1)
                .maxDeliveryFee(10.0)
                .maxDistanceMiles(5.0)
                .isActive(true)
                .build();

        final User user = createTestUser();
        final Store existingStore = createTestStore(1L, "Fresh Mart", 37.7749, -122.4194);
        final UserStore userStore = createTestUserStore(user, existingStore, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findByGooglePlaceId(request.getGooglePlaceId())).thenReturn(Optional.of(existingStore));
        when(userStoreRepository.existsByUserIdAndStoreId(userId, existingStore.getId())).thenReturn(false);
        when(userStoreRepository.save(any(UserStore.class))).thenReturn(userStore);

        // Act
        final UserStoreResponse result = storeService.selectStoreForUser(userId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStore().getName()).isEqualTo("Fresh Mart");
        assertThat(result.getPriority()).isEqualTo(1);
        assertThat(result.getMaxDeliveryFee()).isEqualTo(10.0);

        verify(userRepository).findById(userId);
        verify(storeRepository).findByGooglePlaceId(request.getGooglePlaceId());
        verify(userStoreRepository).existsByUserIdAndStoreId(userId, existingStore.getId());
        verify(userStoreRepository).save(any(UserStore.class));
    }

    @Test
    void shouldCreateNewStoreWhenNotExists() {
        // Arrange
        final Long userId = 1L;
        final StoreSelectionRequest request = StoreSelectionRequest.builder()
                .name("New Store")
                .address("456 Oak St")
                .latitude(37.7750)
                .longitude(-122.4195)
                .phone("(555) 987-6543")
                .googlePlaceId("newplace123")
                .priority(1)
                .isActive(true)
                .build();

        final User user = createTestUser();
        final Store newStore = createTestStore(2L, "New Store", 37.7750, -122.4195);
        final UserStore userStore = createTestUserStore(user, newStore, 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findByGooglePlaceId(request.getGooglePlaceId())).thenReturn(Optional.empty());
        when(storeRepository.save(any(Store.class))).thenReturn(newStore);
        when(userStoreRepository.existsByUserIdAndStoreId(userId, newStore.getId())).thenReturn(false);
        when(userStoreRepository.save(any(UserStore.class))).thenReturn(userStore);

        // Act
        final UserStoreResponse result = storeService.selectStoreForUser(userId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStore().getName()).isEqualTo("New Store");

        verify(userRepository).findById(userId);
        verify(storeRepository).findByGooglePlaceId(request.getGooglePlaceId());
        verify(storeRepository).save(any(Store.class));
        verify(userStoreRepository).save(any(UserStore.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForStoreSelection() {
        // Arrange
        final Long userId = 999L;
        final StoreSelectionRequest request = StoreSelectionRequest.builder()
                .name("Fresh Mart")
                .address("123 Main St")
                .latitude(37.7749)
                .longitude(-122.4194)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> storeService.selectStoreForUser(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found: 999");

        verify(userRepository).findById(userId);
        verify(storeRepository, never()).findByGooglePlaceId(any());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasStore() {
        // Arrange
        final Long userId = 1L;
        final StoreSelectionRequest request = StoreSelectionRequest.builder()
                .name("Fresh Mart")
                .address("123 Main St")
                .latitude(37.7749)
                .longitude(-122.4194)
                .googlePlaceId("place123")
                .build();

        final User user = createTestUser();
        final Store existingStore = createTestStore(1L, "Fresh Mart", 37.7749, -122.4194);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findByGooglePlaceId(request.getGooglePlaceId())).thenReturn(Optional.of(existingStore));
        when(userStoreRepository.existsByUserIdAndStoreId(userId, existingStore.getId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> storeService.selectStoreForUser(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User already has this store");

        verify(userRepository).findById(userId);
        verify(storeRepository).findByGooglePlaceId(request.getGooglePlaceId());
        verify(userStoreRepository).existsByUserIdAndStoreId(userId, existingStore.getId());
        verify(userStoreRepository, never()).save(any());
    }

    @Test
    void shouldAddStoreForUserSuccessfully() {
        // Arrange
        final Long userId = 1L;
        final UserStoreRequest request = UserStoreRequest.builder()
                .storeId(1L)
                .priority(2)
                .maxDeliveryFee(8.0)
                .maxDistanceMiles(3.0)
                .isActive(true)
                .build();

        final User user = createTestUser();
        final Store store = createTestStore(1L, "Fresh Mart", 37.7749, -122.4194);
        final UserStore userStore = UserStore.builder()
                .id(1L)
                .user(user)
                .store(store)
                .priority(2)
                .isActive(true)
                .maxDeliveryFee(8.0)  // Use the value from request
                .maxDistanceMiles(3.0)  // Use the value from request
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(storeRepository.findById(request.getStoreId())).thenReturn(Optional.of(store));
        when(userStoreRepository.existsByUserIdAndStoreId(userId, request.getStoreId())).thenReturn(false);
        when(userStoreRepository.save(any(UserStore.class))).thenReturn(userStore);

        // Act
        final UserStoreResponse result = storeService.addStoreForUser(userId, request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPriority()).isEqualTo(2);
        assertThat(result.getMaxDeliveryFee()).isEqualTo(8.0);

        verify(userRepository).findById(userId);
        verify(storeRepository).findById(request.getStoreId());
        verify(userStoreRepository).existsByUserIdAndStoreId(userId, request.getStoreId());
        verify(userStoreRepository).save(any(UserStore.class));
    }

    // Helper methods
    private User createTestUser() {
        return User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    private Store createTestStore(Long id, String name, Double latitude, Double longitude) {
        return Store.builder()
                .id(id)
                .name(name)
                .address("123 Main St")
                .latitude(latitude)
                .longitude(longitude)
                .phone("(555) 123-4567")
                .active(true)
                .acceptingOrders(true)
                .hasDelivery(true)
                .deliveryFee(5.99)
                .build();
    }

    private UserStore createTestUserStore(User user, Store store, Integer priority) {
        return UserStore.builder()
                .id(1L)
                .user(user)
                .store(store)
                .priority(priority)
                .isActive(true)
                .maxDeliveryFee(10.0)
                .maxDistanceMiles(5.0)
                .build();
    }
} 