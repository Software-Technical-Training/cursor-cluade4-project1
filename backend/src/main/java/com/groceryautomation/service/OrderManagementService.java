package com.groceryautomation.service;

import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.entity.Order;
import com.groceryautomation.entity.User;

import java.util.List;

/**
 * Service for managing the order lifecycle from draft to delivery
 */
public interface OrderManagementService {
    
    /**
     * Create a draft order based on low inventory items
     * 
     * @param user The user for whom to create the order
     * @param lowInventoryItems Items that are running low
     * @return Created draft order with real-time prices
     */
    Order createDraftOrder(User user, List<InventoryItem> lowInventoryItems);
    
    /**
     * Refresh prices for all items in an order
     * 
     * @param orderId The order ID
     * @return Updated order with current prices
     */
    Order refreshPrices(Long orderId);
    
    /**
     * Remove items from a draft order
     * 
     * @param orderId The order ID
     * @param orderItemIds List of order item IDs to remove
     * @return Updated order
     */
    Order removeItems(Long orderId, List<Long> orderItemIds);
    
    /**
     * Update quantity for an order item
     * 
     * @param orderId The order ID
     * @param orderItemId The order item ID
     * @param newQuantity New quantity
     * @return Updated order
     */
    Order updateItemQuantity(Long orderId, Long orderItemId, Double newQuantity);
    
    /**
     * Add an item to a draft order
     * 
     * @param orderId The order ID
     * @param groceryItemId The grocery item to add
     * @param quantity Quantity to add
     * @return Updated order
     */
    Order addItem(Long orderId, Long groceryItemId, Double quantity);
    
    /**
     * Submit a draft order to the store
     * 
     * @param orderId The order ID
     * @return Order with updated status and external order ID
     */
    Order submitOrder(Long orderId);
    
    /**
     * Get all draft orders for a user
     * 
     * @param userId The user ID
     * @return List of draft orders
     */
    List<Order> getUserDraftOrders(Long userId);
    
    /**
     * Get order with price comparison (draft vs current)
     * 
     * @param orderId The order ID
     * @return Order with price comparison data
     */
    Order getOrderWithPriceComparison(Long orderId);
    
    /**
     * Cancel a draft order
     * 
     * @param orderId The order ID
     * @param reason Cancellation reason
     */
    void cancelOrder(Long orderId, String reason);
    
    /**
     * Check and update order status from store API
     * 
     * @param orderId The order ID
     * @return Updated order with current status
     */
    Order updateOrderStatus(Long orderId);
    
    /**
     * Calculate suggested reorder quantities based on consumption patterns
     * 
     * @param inventoryItem Low inventory item
     * @return Suggested quantity to order
     */
    Double calculateSuggestedQuantity(InventoryItem inventoryItem);
    
    /**
     * Get order history for a user
     * 
     * @param userId The user ID
     * @param includeStatus Optional status filter
     * @return List of orders
     */
    List<Order> getOrderHistory(Long userId, String includeStatus);
} 