package com.groceryautomation.enums;

/**
 * Types of notifications that can be sent to users
 */
public enum NotificationType {
    // Inventory related
    LOW_INVENTORY("Low Inventory Alert", "Some items in your inventory are running low"),
    OUT_OF_STOCK("Out of Stock Alert", "Some items are completely out of stock"),
    
    // Order related
    DRAFT_ORDER_CREATED("Draft Order Created", "A new order has been created for your review"),
    PRICE_CHANGE("Price Change Alert", "Prices have changed for items in your order"),
    ORDER_SUBMITTED("Order Submitted", "Your order has been submitted to the store"),
    ORDER_CONFIRMED("Order Confirmed", "Your order has been confirmed by the store"),
    ORDER_READY("Order Ready", "Your order is ready for delivery"),
    ORDER_DELIVERED("Order Delivered", "Your order has been delivered"),
    ORDER_CANCELLED("Order Cancelled", "Your order has been cancelled"),
    
    // System related
    DEVICE_OFFLINE("Device Offline", "Your device has gone offline"),
    DEVICE_ONLINE("Device Online", "Your device is back online"),
    SYSTEM_MAINTENANCE("System Maintenance", "System maintenance scheduled");
    
    private final String title;
    private final String defaultMessage;
    
    NotificationType(String title, String defaultMessage) {
        this.title = title;
        this.defaultMessage = defaultMessage;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
} 