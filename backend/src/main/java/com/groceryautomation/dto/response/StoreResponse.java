package com.groceryautomation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponse {
    
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private boolean active;
    private boolean acceptingOrders;
    private Double distanceInMiles; // Distance from user's location
    private boolean hasDelivery;
    private boolean hasPickup;
    private Double deliveryFee;
    private Double minimumOrderAmount;
} 