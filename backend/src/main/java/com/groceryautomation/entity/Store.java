package com.groceryautomation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Store name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 500)
    private String address;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    private String phone;
    
    private String email;
    
    // Google Place ID to avoid duplicates
    @Column(unique = true)
    private String googlePlaceId;
    
    // Store hours
    private LocalTime openingTime;
    private LocalTime closingTime;
    
    @Builder.Default
    private boolean active = true;
    
    @Builder.Default
    private boolean acceptingOrders = true;
    
    // For calculating distance from user
    @Transient
    private Double distanceInMiles;
    
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<UserStore> userStores = new ArrayList<>();
    
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
    
    // Store capabilities
    @Builder.Default
    private boolean hasDelivery = true;
    
    @Builder.Default
    private boolean hasPickup = true;
    
    @Builder.Default
    private Double deliveryFee = 5.99;
    
    @Builder.Default
    private Double minimumOrderAmount = 25.00;
} 