package com.groceryautomation.service.impl;

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
import com.groceryautomation.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StoreServiceImpl implements StoreService {
    
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final UserStoreRepository userStoreRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<StoreResponse> findNearbyStores(Double latitude, Double longitude, Double radius, Integer limit) {
        List<Store> stores = storeRepository.findNearbyStores(latitude, longitude, radius, limit);
        
        // Calculate distance for each store
        return stores.stream()
                .map(store -> {
                    StoreResponse response = mapToResponse(store);
                    response.setDistanceInMiles(calculateDistance(latitude, longitude, 
                            store.getLatitude(), store.getLongitude()));
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StoreResponse> getAllActiveStores() {
        List<Store> stores = storeRepository.findByActiveTrue();
        return stores.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public StoreResponse getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found: " + id));
        return mapToResponse(store);
    }
    
    @Override
    public UserStoreResponse selectStoreForUser(Long userId, StoreSelectionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Store store;
        
        // Check if store already exists by Google Place ID
        if (request.getGooglePlaceId() != null) {
            store = storeRepository.findByGooglePlaceId(request.getGooglePlaceId())
                    .orElse(null);
        } else {
            // Try to find by name and address
            store = storeRepository.findByNameAndAddress(request.getName(), request.getAddress())
                    .orElse(null);
        }
        
        // Create store if it doesn't exist
        if (store == null) {
            store = Store.builder()
                    .name(request.getName())
                    .address(request.getAddress())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .phone(request.getPhone())
                    .googlePlaceId(request.getGooglePlaceId())
                    .active(true)
                    .acceptingOrders(true)
                    .build();
            
            store = storeRepository.save(store);
            log.info("Created new store '{}' from Google Maps selection", store.getName());
        }
        
        // Check if user already has this store
        if (userStoreRepository.existsByUserIdAndStoreId(userId, store.getId())) {
            throw new RuntimeException("User already has this store");
        }
        
        // Determine priority
        Integer priority = request.getPriority();
        if (priority == null) {
            // Auto-assign next available priority
            priority = userStoreRepository.findMaxPriorityByUserId(userId) + 1;
        } else {
            // If priority is specified, shift existing stores if needed
            userStoreRepository.incrementPrioritiesFrom(userId, priority);
        }
        
        UserStore userStore = UserStore.builder()
                .user(user)
                .store(store)
                .priority(priority)
                .isActive(request.isActive())
                .maxDeliveryFee(request.getMaxDeliveryFee())
                .maxDistanceMiles(request.getMaxDistanceMiles())
                .notes(request.getNotes())
                .build();
        
        UserStore saved = userStoreRepository.save(userStore);
        log.info("Added store '{}' with priority {} for user '{}'", 
                store.getName(), priority, user.getEmail());
        
        return mapToUserStoreResponse(saved);
    }
    
    @Override
    public UserStoreResponse addStoreForUser(Long userId, UserStoreRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found: " + request.getStoreId()));
        
        if (!store.isActive()) {
            throw new RuntimeException("Store is not active");
        }
        
        // Check if user already has this store
        if (userStoreRepository.existsByUserIdAndStoreId(userId, request.getStoreId())) {
            throw new RuntimeException("User already has this store");
        }
        
        // Determine priority
        Integer priority = request.getPriority();
        if (priority == null) {
            // Auto-assign next available priority
            priority = userStoreRepository.findMaxPriorityByUserId(userId) + 1;
        } else {
            // If priority is specified, shift existing stores if needed
            userStoreRepository.incrementPrioritiesFrom(userId, priority);
        }
        
        UserStore userStore = UserStore.builder()
                .user(user)
                .store(store)
                .priority(priority)
                .isActive(request.isActive())
                .maxDeliveryFee(request.getMaxDeliveryFee())
                .maxDistanceMiles(request.getMaxDistanceMiles())
                .notes(request.getNotes())
                .build();
        
        UserStore saved = userStoreRepository.save(userStore);
        log.info("Added store '{}' with priority {} for user '{}'", 
                store.getName(), priority, user.getEmail());
        
        return mapToUserStoreResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserStoreResponse> getUserStores(Long userId) {
        List<UserStore> userStores = userStoreRepository.findByUserIdOrderByPriorityAsc(userId);
        return userStores.stream()
                .map(this::mapToUserStoreResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public UserStoreResponse updateUserStore(Long userId, Long storeId, UserStoreRequest request) {
        UserStore userStore = userStoreRepository.findByUserIdAndStoreId(userId, storeId)
                .orElseThrow(() -> new RuntimeException("User store relationship not found"));
        
        // Update fields if provided
        if (request.getMaxDeliveryFee() != null) {
            userStore.setMaxDeliveryFee(request.getMaxDeliveryFee());
        }
        if (request.getMaxDistanceMiles() != null) {
            userStore.setMaxDistanceMiles(request.getMaxDistanceMiles());
        }
        if (request.getNotes() != null) {
            userStore.setNotes(request.getNotes());
        }
        userStore.setActive(request.isActive());
        
        UserStore updated = userStoreRepository.save(userStore);
        log.info("Updated user store relationship for user {} and store {}", userId, storeId);
        
        return mapToUserStoreResponse(updated);
    }
    
    @Override
    public void removeStoreForUser(Long userId, Long storeId) {
        UserStore userStore = userStoreRepository.findByUserIdAndStoreId(userId, storeId)
                .orElseThrow(() -> new RuntimeException("User store relationship not found"));
        
        Integer priority = userStore.getPriority();
        
        // Delete the relationship
        userStoreRepository.delete(userStore);
        
        // Adjust priorities of remaining stores
        userStoreRepository.decrementPrioritiesAfter(userId, priority);
        
        log.info("Removed store {} from user {} and adjusted priorities", storeId, userId);
    }
    
    @Override
    public UserStoreResponse setPrimaryStore(Long userId, Long storeId) {
        UserStore userStore = userStoreRepository.findByUserIdAndStoreId(userId, storeId)
                .orElseThrow(() -> new RuntimeException("User store relationship not found"));
        
        if (userStore.getPriority() == 1) {
            // Already primary
            return mapToUserStoreResponse(userStore);
        }
        
        // Get current primary store
        UserStore currentPrimary = userStoreRepository.findByUserIdAndPriority(userId, 1)
                .orElse(null);
        
        if (currentPrimary != null) {
            // Swap priorities
            currentPrimary.setPriority(userStore.getPriority());
            userStoreRepository.save(currentPrimary);
        }
        
        userStore.setPriority(1);
        UserStore updated = userStoreRepository.save(userStore);
        
        log.info("Set store {} as primary for user {}", storeId, userId);
        
        return mapToUserStoreResponse(updated);
    }
    
    @Override
    public List<UserStoreResponse> reorderUserStores(Long userId, List<Long> storeIds) {
        List<UserStore> userStores = userStoreRepository.findByUserIdOrderByPriorityAsc(userId);
        
        // Validate all store IDs belong to user
        List<Long> currentStoreIds = userStores.stream()
                .map(us -> us.getStore().getId())
                .collect(Collectors.toList());
        
        if (!currentStoreIds.containsAll(storeIds) || !storeIds.containsAll(currentStoreIds)) {
            throw new RuntimeException("Store IDs do not match user's current stores");
        }
        
        // Update priorities based on order in the list
        for (int i = 0; i < storeIds.size(); i++) {
            Long storeId = storeIds.get(i);
            UserStore userStore = userStores.stream()
                    .filter(us -> us.getStore().getId().equals(storeId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Store not found: " + storeId));
            
            userStore.setPriority(i + 1);
            userStoreRepository.save(userStore);
        }
        
        log.info("Reordered stores for user {}", userId);
        
        return getUserStores(userId);
    }
    
    private StoreResponse mapToResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .phone(store.getPhone())
                .email(store.getEmail())
                .openingTime(store.getOpeningTime())
                .closingTime(store.getClosingTime())
                .active(store.isActive())
                .acceptingOrders(store.isAcceptingOrders())
                .hasDelivery(store.isHasDelivery())
                .hasPickup(store.isHasPickup())
                .deliveryFee(store.getDeliveryFee())
                .minimumOrderAmount(store.getMinimumOrderAmount())
                .build();
    }
    
    // Calculate distance using Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 3959; // miles
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(earthRadius * c * 100.0) / 100.0; // Round to 2 decimal places
    }
    
    private UserStoreResponse mapToUserStoreResponse(UserStore userStore) {
        return UserStoreResponse.builder()
                .id(userStore.getId())
                .userId(userStore.getUser().getId())
                .userEmail(userStore.getUser().getEmail())
                .store(mapToResponse(userStore.getStore()))
                .priority(userStore.getPriority())
                .isPrimary(userStore.isPrimary())
                .isActive(userStore.isActive())
                .maxDeliveryFee(userStore.getMaxDeliveryFee())
                .maxDistanceMiles(userStore.getMaxDistanceMiles())
                .notes(userStore.getNotes())
                .addedAt(userStore.getAddedAt())
                .build();
    }
} 