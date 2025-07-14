package com.groceryautomation.entity;

import com.groceryautomation.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @NotBlank(message = "Notification type is required")
    @Column(nullable = false, length = 50)
    private String type; // LOW_INVENTORY, DRAFT_ORDER_CREATED, PRICE_CHANGE, ORDER_CONFIRMED, etc.
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    // Link to related entity
    @Column(length = 50)
    private String relatedEntityType; // ORDER, INVENTORY_ITEM, etc.
    
    private Long relatedEntityId;
    
    @Builder.Default
    @Column(nullable = false)
    private boolean read = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime readAt;
    
    // Email/SMS notification tracking
    @Builder.Default
    private boolean emailSent = false;
    
    @Builder.Default
    private boolean smsSent = false;
    
    private LocalDateTime emailSentAt;
    private LocalDateTime smsSentAt;
    
    // Helper method to mark as read
    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }
} 