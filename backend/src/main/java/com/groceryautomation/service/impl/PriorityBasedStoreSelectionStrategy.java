package com.groceryautomation.service.impl;

import com.groceryautomation.entity.GroceryItem;
import com.groceryautomation.entity.Store;
import com.groceryautomation.entity.User;
import com.groceryautomation.entity.UserStore;
import com.groceryautomation.service.StoreSelectionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriorityBasedStoreSelectionStrategy implements StoreSelectionStrategy {
    
    @Override
    public Store selectStore(User user, List<GroceryItem> items) {
        // Get user's active stores ordered by priority
        List<UserStore> activeStores = user.getUserStores().stream()
                .filter(UserStore::isActive)
                .sorted((a, b) -> a.getPriority().compareTo(b.getPriority()))
                .collect(Collectors.toList());
        
        if (activeStores.isEmpty()) {
            log.warn("No active stores found for user: {}", user.getEmail());
            return null;
        }
        
        // For now, return the primary store (priority = 1)
        // In the full implementation, the OrderManagementService will check
        // availability via store APIs and create orders from multiple stores if needed
        Store primaryStore = activeStores.get(0).getStore();
        log.info("Selected primary store '{}' for user '{}'", 
                primaryStore.getName(), user.getEmail());
        return primaryStore;
    }
    
    @Override
    public List<StoreSelection> selectStoresForItems(User user, List<GroceryItem> items) {
        Store store = selectStore(user, items);
        
        if (store == null) {
            return new ArrayList<>();
        }
        
        // For now, assign all items to the primary store
        // The OrderManagementService will handle splitting orders across stores
        // based on actual availability from store APIs
        List<StoreSelection> selections = new ArrayList<>();
        selections.add(new StoreSelection(store, new ArrayList<>(items)));
        
        log.info("Assigned all {} items to primary store '{}' for initial selection", 
                items.size(), store.getName());
        
        return selections;
    }
} 