package com.groceryautomation.service;

import com.groceryautomation.entity.Store;
import com.groceryautomation.entity.User;
import com.groceryautomation.entity.GroceryItem;

import java.util.List;

public interface StoreSelectionStrategy {
    
    /**
     * Select the best store for an order based on the strategy implementation
     * 
     * @param user The user placing the order
     * @param items The items to be ordered
     * @return The selected store, or null if no suitable store found
     */
    Store selectStore(User user, List<GroceryItem> items);
    
    /**
     * Select stores for items that might need to be split across multiple stores
     * 
     * @param user The user placing the order
     * @param items The items to be ordered
     * @return List of stores with their assigned items
     */
    List<StoreSelection> selectStoresForItems(User user, List<GroceryItem> items);
    
    /**
     * Result class for store selection with items
     */
    class StoreSelection {
        private final Store store;
        private final List<GroceryItem> items;
        
        public StoreSelection(Store store, List<GroceryItem> items) {
            this.store = store;
            this.items = items;
        }
        
        public Store getStore() {
            return store;
        }
        
        public List<GroceryItem> getItems() {
            return items;
        }
    }
} 