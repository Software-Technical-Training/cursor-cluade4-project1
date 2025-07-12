package com.groceryautomation.entity;

import com.groceryautomation.enums.InventoryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grocery_item_id", nullable = false)
    private GroceryItem groceryItem;
    
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    @Column(nullable = false)
    @Builder.Default
    private Double quantity = 0.0;
    
    @NotNull(message = "Threshold is required")
    @PositiveOrZero(message = "Threshold must be zero or positive")
    @Column(nullable = false)
    private Double thresholdQuantity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.SUFFICIENT;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    // Track when item was added to inventory
    private LocalDateTime addedAt;
    
    // Track expiration for perishables
    private LocalDateTime expirationDate;
    
    // Helper method to calculate status based on quantity and threshold
    @PrePersist
    @PreUpdate
    public void updateStatus() {
        if (quantity == 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        } else if (quantity <= thresholdQuantity * 0.5) {
            this.status = InventoryStatus.CRITICAL;
        } else if (quantity <= thresholdQuantity) {
            this.status = InventoryStatus.LOW;
        } else {
            this.status = InventoryStatus.SUFFICIENT;
        }
    }
} 