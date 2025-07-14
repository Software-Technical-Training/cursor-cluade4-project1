package com.groceryautomation.service;

import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.entity.Order;
import com.groceryautomation.entity.User;
import com.groceryautomation.entity.Notification;
import com.groceryautomation.enums.NotificationType;

import java.util.List;

/**
 * Service for managing user notifications
 */
public interface NotificationService {
    
    /**
     * Notify user about low inventory items
     */
    void notifyLowInventory(User user, List<InventoryItem> lowItems);
    
    /**
     * Notify user that a draft order has been created
     */
    void notifyDraftOrderCreated(User user, Order draftOrder);
    
    /**
     * Notify user about price changes in their order
     */
    void notifyPriceChanges(User user, Order order, List<String> changedItems);
    
    /**
     * Notify user that their order has been confirmed
     */
    void notifyOrderConfirmed(User user, Order order);
    
    /**
     * Notify user that their order has been delivered
     */
    void notifyOrderDelivered(User user, Order order);
    
    /**
     * Create a custom notification
     */
    Notification createNotification(User user, NotificationType type, String title, String message, 
                                  String relatedEntityType, Long relatedEntityId);
    
    /**
     * Get all notifications for a user
     */
    List<Notification> getUserNotifications(Long userId);
    
    /**
     * Get unread notifications for a user
     */
    List<Notification> getUnreadNotifications(Long userId);
    
    /**
     * Mark a notification as read
     */
    void markAsRead(Long notificationId);
    
    /**
     * Mark all notifications as read for a user
     */
    void markAllAsRead(Long userId);
    
    /**
     * Get unread notification count for a user
     */
    Long getUnreadCount(Long userId);
    
    /**
     * Delete old notifications (cleanup job)
     */
    void deleteOldNotifications(int daysToKeep);
    
    /**
     * Send email notification (if enabled)
     */
    void sendEmailNotification(Notification notification);
    
    /**
     * Send SMS notification (if enabled)
     */
    void sendSmsNotification(Notification notification);
} 