package com.groceryautomation.enums;

/**
 * Represents the lifecycle status of an order
 */
public enum OrderStatus {
    DRAFT("Draft", "Order created by system, awaiting user review"),
    USER_MODIFIED("Modified", "User has made changes to the order"),
    SUBMITTED("Submitted", "User approved and submitted to store"),
    CONFIRMED("Confirmed", "Store accepted the order"),
    IN_PROGRESS("In Progress", "Order is being prepared"),
    OUT_FOR_DELIVERY("Out for Delivery", "Order is on the way"),
    DELIVERED("Delivered", "Order has been delivered"),
    CANCELLED("Cancelled", "Order cancelled by user or store"),
    FAILED("Failed", "Order failed due to payment or other issues");
    
    private final String displayName;
    private final String description;
    
    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if the order is in a state where user can modify it
     */
    public boolean isModifiable() {
        return this == DRAFT || this == USER_MODIFIED;
    }
    
    /**
     * Check if the order is in a final state
     */
    public boolean isFinalState() {
        return this == DELIVERED || this == CANCELLED || this == FAILED;
    }
} 