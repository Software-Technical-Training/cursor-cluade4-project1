package com.groceryautomation.graphql.resolver.query;

import com.groceryautomation.dto.response.StoreResponse;
import com.groceryautomation.entity.Store;
import com.groceryautomation.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StoreQueryResolver {
    
    private final StoreService storeService;
    
    @QueryMapping
    public Store store(@Argument Long id) {
        log.info("Fetching store with ID: {} from GraphQL", id);
        try {
            StoreResponse storeResponse = storeService.getStoreById(id);
            return convertToEntity(storeResponse);
        } catch (Exception e) {
            log.error("Error fetching store {}: {}", id, e.getMessage());
            return null;
        }
    }
    
    @QueryMapping
    public List<Store> nearbyStores(@Argument Double latitude, 
                                    @Argument Double longitude, 
                                    @Argument Double radius, 
                                    @Argument Integer limit) {
        log.info("Finding stores near lat: {}, lon: {}, within {} miles from GraphQL", 
                latitude, longitude, radius);
        try {
            return storeService.findNearbyStores(latitude, longitude, radius, limit).stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding nearby stores: {}", e.getMessage());
            return List.of();
        }
    }
    
    @QueryMapping
    public List<Store> allActiveStores() {
        log.info("Fetching all active stores from GraphQL");
        try {
            return storeService.getAllActiveStores().stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching active stores: {}", e.getMessage());
            return List.of();
        }
    }
    
    private Store convertToEntity(StoreResponse response) {
        return Store.builder()
                .id(response.getId())
                .name(response.getName())
                .address(response.getAddress())
                .latitude(response.getLatitude())
                .longitude(response.getLongitude())
                .phone(response.getPhone())
                .email(response.getEmail())
                .openingTime(response.getOpeningTime())
                .closingTime(response.getClosingTime())
                .active(response.isActive())
                .acceptingOrders(response.isAcceptingOrders())
                .distanceInMiles(response.getDistanceInMiles())
                .hasDelivery(response.isHasDelivery())
                .hasPickup(response.isHasPickup())
                .deliveryFee(response.getDeliveryFee())
                .minimumOrderAmount(response.getMinimumOrderAmount())
                .build();
    }
}