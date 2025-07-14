package com.groceryautomation.service;

import com.groceryautomation.dto.request.StoreSelectionRequest;
import com.groceryautomation.dto.request.UserStoreRequest;
import com.groceryautomation.dto.response.StoreResponse;
import com.groceryautomation.dto.response.UserStoreResponse;

import java.util.List;

public interface StoreService {
    
    List<StoreResponse> findNearbyStores(Double latitude, Double longitude, Double radius, Integer limit);
    
    List<StoreResponse> getAllActiveStores();
    
    StoreResponse getStoreById(Long id);
    
    // User-Store relationship management
    UserStoreResponse addStoreForUser(Long userId, UserStoreRequest request);
    
    UserStoreResponse selectStoreForUser(Long userId, StoreSelectionRequest request);
    
    List<UserStoreResponse> getUserStores(Long userId);
    
    UserStoreResponse updateUserStore(Long userId, Long storeId, UserStoreRequest request);
    
    void removeStoreForUser(Long userId, Long storeId);
    
    UserStoreResponse setPrimaryStore(Long userId, Long storeId);
    
    List<UserStoreResponse> reorderUserStores(Long userId, List<Long> storeIds);
} 