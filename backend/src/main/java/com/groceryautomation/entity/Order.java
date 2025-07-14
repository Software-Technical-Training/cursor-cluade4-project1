package com.groceryautomation.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.groceryautomation.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.DRAFT;
    
    @NotNull(message = "Subtotal is required")
    @Positive(message = "Subtotal must be positive")
    @Column(nullable = false)
    @Builder.Default
    private Double subtotal = 0.0;
    
    @Column(nullable = false)
    @Builder.Default
    private Double deliveryFee = 0.0;
    
    @Column(nullable = false)
    @Builder.Default
    private Double tax = 0.0;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    @Column(nullable = false)
    @Builder.Default
    private Double totalAmount = 0.0;
    
    // Draft order pricing information
    private Double estimatedTotal;  // Initial estimate when draft created
    private Double finalTotal;      // After user modifications
    
    // Timestamps for order lifecycle
    private LocalDateTime draftCreatedAt;
    private LocalDateTime userReviewedAt;
    private LocalDateTime submittedAt;
    
    // External order ID from store's system
    private String externalOrderId;
    
    // Notification sent flag
    @Builder.Default
    private boolean notificationSent = false;
    
    // Delivery information
    @Column(length = 500)
    private String deliveryAddress;
    
    @Column(length = 1000)
    private String deliveryInstructions;
    
    private LocalDateTime scheduledDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    
    // Payment information (mock for POC)
    @Builder.Default
    private String paymentMethod = "Mock Credit Card";
    
    @Builder.Default
    private boolean isPaid = true; // Always true for POC
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Tracking information
    private String trackingNumber;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    
    // Helper method to calculate totals
    @PrePersist
    @PreUpdate
    public void calculateTotals() {
        // Only recalculate if we have items, otherwise preserve explicitly set values
        if (items != null && !items.isEmpty()) {
            this.subtotal = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
            this.tax = this.subtotal * 0.08; // 8% tax for POC
            this.totalAmount = this.subtotal + this.deliveryFee + this.tax;
        }
    }
} 