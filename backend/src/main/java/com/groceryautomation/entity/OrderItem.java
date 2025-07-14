package com.groceryautomation.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grocery_item_id", nullable = false)
    private GroceryItem groceryItem;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Column(nullable = false)
    private Double quantity;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price; // Price at time of order
    
    // Store integration fields (using universal SKU from GroceryItem)
    private Double priceAtCreation; // Price when draft was created
    private Double currentPrice;    // Latest price from store API
    
    // User modification tracking
    @Builder.Default
    private boolean userRemoved = false;    // Flag for items user removed
    @Builder.Default
    private boolean priceChanged = false;   // Flag if price changed since draft
    @Builder.Default
    private boolean quantityModified = false; // Flag if user changed quantity
    private Double originalQuantity;        // Original quantity suggested by system
    
    @Column(nullable = false)
    @Builder.Default
    private Double subtotal = 0.0;
    
    // Notes for special requests
    private String notes;
    
    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        this.subtotal = this.quantity * this.price;
    }
} 