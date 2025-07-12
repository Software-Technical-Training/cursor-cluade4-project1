package com.groceryautomation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Device ID is required")
    @Column(nullable = false, unique = true)
    private String deviceId;
    
    @Column(nullable = false)
    @Builder.Default
    private String name = "Smart Fridge Sensor";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InventoryItem> inventoryItems = new ArrayList<>();
    
    private LocalDateTime lastSync;
    
    @Builder.Default
    private boolean active = true;
    
    @Builder.Default
    private boolean online = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Mock sensor configuration
    @Builder.Default
    private Integer mockDataIntervalSeconds = 60; // How often to generate mock data
    
    @Builder.Default
    private Double mockConsumptionRate = 0.1; // Rate at which items are consumed
} 