package com.groceryautomation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_stores", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "store_id"}),
           @UniqueConstraint(columnNames = {"user_id", "priority"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @NotNull(message = "Store is required")
    private Store store;
    
    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    @Column(nullable = false)
    private Integer priority; // 1 = primary, 2+ = backup stores in order
    
    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime addedAt;
    
    // Store-specific user preferences
    private Double maxDeliveryFee; // User's max acceptable delivery fee for this store
    private Double maxDistanceMiles; // How far user willing to order from
    private String notes; // User notes about this store
    
    // Helper method to check if this is the primary store
    public boolean isPrimary() {
        return priority != null && priority == 1;
    }
} 