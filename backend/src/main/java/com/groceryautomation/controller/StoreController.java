package com.groceryautomation.controller;

import com.groceryautomation.dto.request.StoreSelectionRequest;
import com.groceryautomation.dto.request.UserStoreRequest;
import com.groceryautomation.dto.response.ApiResponse;
import com.groceryautomation.dto.response.StoreResponse;
import com.groceryautomation.dto.response.UserStoreResponse;
import com.groceryautomation.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Store Management", description = "Endpoints for finding and selecting stores")
public class StoreController {
    
    private final StoreService storeService;
    
    @GetMapping("/nearby")
    @Operation(summary = "Find nearby stores", description = "Find stores near a given location within a radius")
    public ResponseEntity<ApiResponse<List<StoreResponse>>> findNearbyStores(
            @Parameter(description = "Latitude", example = "37.7749") @RequestParam Double latitude,
            @Parameter(description = "Longitude", example = "-122.4194") @RequestParam Double longitude,
            @Parameter(description = "Search radius in miles", example = "5.0") @RequestParam(defaultValue = "5.0") Double radius,
            @Parameter(description = "Maximum results to return", example = "5") @RequestParam(defaultValue = "5") Integer limit) {
        
        log.info("Finding stores near lat: {}, lon: {}, within {} miles", latitude, longitude, radius);
        List<StoreResponse> stores = storeService.findNearbyStores(latitude, longitude, radius, limit);
        return ResponseEntity.ok(ApiResponse.success(stores, 
                String.format("Found %d stores within %.1f miles", stores.size(), radius)));
    }
    
    @GetMapping
    @Operation(summary = "Get all active stores", description = "Get all active stores in the system")
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getAllActiveStores() {
        log.info("Fetching all active stores");
        List<StoreResponse> stores = storeService.getAllActiveStores();
        return ResponseEntity.ok(ApiResponse.success(stores));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get store by ID", description = "Get store details by store ID")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(
            @Parameter(description = "Store ID") @PathVariable Long id) {
        log.info("Fetching store with ID: {}", id);
        try {
            StoreResponse store = storeService.getStoreById(id);
            return ResponseEntity.ok(ApiResponse.success(store));
        } catch (RuntimeException e) {
            log.error("Error fetching store: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // User-Store relationship management endpoints
    
    @PostMapping("/user/{userId}/select-store")
    @Operation(summary = "Select store from Google Maps", description = "Select a store from Google Maps search results and add to user's list")
    public ResponseEntity<ApiResponse<UserStoreResponse>> selectStoreForUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody StoreSelectionRequest request) {
        log.info("User {} selecting store: {}", userId, request.getName());
        try {
            UserStoreResponse userStore = storeService.selectStoreForUser(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(userStore, "Store selected successfully"));
        } catch (RuntimeException e) {
            log.error("Error selecting store: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/user/{userId}/stores")
    @Operation(summary = "Add existing store for user", description = "Add an existing store to user's list of stores by storeId")
    public ResponseEntity<ApiResponse<UserStoreResponse>> addStoreForUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody UserStoreRequest request) {
        log.info("Adding store {} for user {}", request.getStoreId(), userId);
        try {
            UserStoreResponse userStore = storeService.addStoreForUser(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(userStore, "Store added successfully"));
        } catch (RuntimeException e) {
            log.error("Error adding store: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}/stores")
    @Operation(summary = "Get user stores", description = "Get all stores for a user ordered by priority")
    public ResponseEntity<ApiResponse<List<UserStoreResponse>>> getUserStores(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Fetching stores for user {}", userId);
        List<UserStoreResponse> stores = storeService.getUserStores(userId);
        return ResponseEntity.ok(ApiResponse.success(stores));
    }
    
    @PutMapping("/user/{userId}/stores/{storeId}")
    @Operation(summary = "Update user store preferences", description = "Update user's preferences for a specific store")
    public ResponseEntity<ApiResponse<UserStoreResponse>> updateUserStore(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Store ID") @PathVariable Long storeId,
            @Valid @RequestBody UserStoreRequest request) {
        log.info("Updating store {} preferences for user {}", storeId, userId);
        try {
            UserStoreResponse userStore = storeService.updateUserStore(userId, storeId, request);
            return ResponseEntity.ok(ApiResponse.success(userStore, "Store preferences updated"));
        } catch (RuntimeException e) {
            log.error("Error updating store preferences: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/user/{userId}/stores/{storeId}")
    @Operation(summary = "Remove store from user", description = "Remove a store from user's list")
    public ResponseEntity<ApiResponse<Void>> removeStoreForUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Store ID") @PathVariable Long storeId) {
        log.info("Removing store {} from user {}", storeId, userId);
        try {
            storeService.removeStoreForUser(userId, storeId);
            return ResponseEntity.ok(ApiResponse.success(null, "Store removed successfully"));
        } catch (RuntimeException e) {
            log.error("Error removing store: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/user/{userId}/stores/{storeId}/set-primary")
    @Operation(summary = "Set primary store", description = "Set a store as the user's primary store")
    public ResponseEntity<ApiResponse<UserStoreResponse>> setPrimaryStore(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Store ID") @PathVariable Long storeId) {
        log.info("Setting store {} as primary for user {}", storeId, userId);
        try {
            UserStoreResponse userStore = storeService.setPrimaryStore(userId, storeId);
            return ResponseEntity.ok(ApiResponse.success(userStore, "Primary store updated"));
        } catch (RuntimeException e) {
            log.error("Error setting primary store: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/user/{userId}/stores/reorder")
    @Operation(summary = "Reorder user stores", description = "Reorder user's stores by providing store IDs in desired order")
    public ResponseEntity<ApiResponse<List<UserStoreResponse>>> reorderUserStores(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Store IDs in desired order") @RequestBody List<Long> storeIds) {
        log.info("Reordering stores for user {}", userId);
        try {
            List<UserStoreResponse> stores = storeService.reorderUserStores(userId, storeIds);
            return ResponseEntity.ok(ApiResponse.success(stores, "Stores reordered successfully"));
        } catch (RuntimeException e) {
            log.error("Error reordering stores: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 