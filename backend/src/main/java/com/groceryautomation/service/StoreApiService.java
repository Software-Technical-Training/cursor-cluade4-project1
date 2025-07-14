package com.groceryautomation.service;

import com.groceryautomation.dto.ProductPrice;
import com.groceryautomation.entity.Order;

import java.util.List;
import java.util.Map;

/**
 * Interface for integrating with external store APIs
 */
public interface StoreApiService {
    
    /**
     * Fetch current prices for a list of products from a specific store
     * 
     * @param storeId The store ID in our system
     * @param skus List of universal SKUs for the products
     * @return Map of SKU to ProductPrice
     */
    Map<String, ProductPrice> fetchPrices(Long storeId, List<String> skus);
    
    /**
     * Submit an order to the store's system
     * 
     * @param order The order to submit
     * @return External order ID from the store's system
     */
    String submitOrder(Order order);
    
    /**
     * Check the status of an order in the store's system
     * 
     * @param storeId The store ID
     * @param externalOrderId The order ID in the store's system
     * @return Current order status
     */
    String checkOrderStatus(Long storeId, String externalOrderId);
    
    /**
     * Search for a product in the store's catalog
     * 
     * @param storeId The store ID
     * @param searchTerm Product search term
     * @return List of matching products with prices
     */
    List<ProductPrice> searchProducts(Long storeId, String searchTerm);
    
    /**
     * Check if the store's API is available
     * 
     * @param storeId The store ID
     * @return true if API is accessible
     */
    boolean isStoreApiAvailable(Long storeId);
    
    /**
     * Get store-specific product ID mapping
     * 
     * @param storeId The store ID
     * @param barcode Product barcode
     * @return Store's product ID if found
     */
    String getStoreProductId(Long storeId, String barcode);
} 