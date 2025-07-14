package com.groceryautomation.service.impl;

import com.groceryautomation.dto.ProductPrice;
import com.groceryautomation.entity.Order;
import com.groceryautomation.entity.Store;
import com.groceryautomation.service.StoreApiService;
import com.groceryautomation.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock implementation of store API service for development and testing
 */
@Service
@Profile("!production")
@RequiredArgsConstructor
@Slf4j
public class MockStoreApiService implements StoreApiService {
    
    private final StoreRepository storeRepository;
    
    // Mock price variations to simulate real-world scenarios
    private static final double PRICE_VARIATION_PERCENT = 0.15; // ±15% price variation
    private static final double SALE_PROBABILITY = 0.3; // 30% chance of sale
    private static final double OUT_OF_STOCK_PROBABILITY = 0.05; // 5% chance out of stock
    
    @Override
    public Map<String, ProductPrice> fetchPrices(Long storeId, List<String> skus) {
        log.info("Fetching prices for {} products from store {}", skus.size(), storeId);
        
        Map<String, ProductPrice> prices = new HashMap<>();
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new RuntimeException("Store not found"));
        
        for (String sku : skus) {
            prices.put(sku, generateMockPrice(sku, store));
        }
        
        return prices;
    }
    
    @Override
    public String submitOrder(Order order) {
        log.info("Submitting order {} to store {}", order.getOrderNumber(), order.getStore().getName());
        
        // Simulate API processing time
        try {
            Thread.sleep(500 + ThreadLocalRandom.current().nextInt(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Generate external order ID
        String externalOrderId = "EXT-" + order.getStore().getId() + "-" + System.currentTimeMillis();
        log.info("Order submitted successfully. External ID: {}", externalOrderId);
        
        return externalOrderId;
    }
    
    @Override
    public String checkOrderStatus(Long storeId, String externalOrderId) {
        log.info("Checking order status for {} at store {}", externalOrderId, storeId);
        
        // Simulate status progression based on order age
        String[] statuses = {"CONFIRMED", "IN_PROGRESS", "OUT_FOR_DELIVERY", "DELIVERED"};
        int randomIndex = ThreadLocalRandom.current().nextInt(statuses.length);
        
        return statuses[randomIndex];
    }
    
    @Override
    public List<ProductPrice> searchProducts(Long storeId, String searchTerm) {
        log.info("Searching for '{}' at store {}", searchTerm, storeId);
        
        // Return mock search results
        List<ProductPrice> results = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            results.add(generateMockSearchResult(searchTerm, i));
        }
        
        return results;
    }
    
    @Override
    public boolean isStoreApiAvailable(Long storeId) {
        // Simulate 95% uptime
        return ThreadLocalRandom.current().nextDouble() > 0.05;
    }
    
    @Override
    public String getStoreProductId(Long storeId, String barcode) {
        // Generate consistent store product ID based on barcode and store
        return "SP-" + storeId + "-" + barcode.hashCode();
    }
    
    private ProductPrice generateMockPrice(String sku, Store store) {
        Random random = ThreadLocalRandom.current();
        
        // Base price between $0.99 and $19.99
        double basePrice = 0.99 + (19.0 * random.nextDouble());
        double regularPrice = Math.round(basePrice * 100.0) / 100.0;
        
        // Apply price variation
        double variation = 1 + ((random.nextDouble() - 0.5) * PRICE_VARIATION_PERCENT);
        regularPrice = Math.round(regularPrice * variation * 100.0) / 100.0;
        
        ProductPrice.ProductPriceBuilder builder = ProductPrice.builder()
            .sku(sku)
            .productName("Product " + sku)
            .regularPrice(regularPrice)
            .inStock(random.nextDouble() > OUT_OF_STOCK_PROBABILITY)
            .stockQuantity(random.nextInt(100))
            .unit("each")
            .priceValidUntil(LocalDateTime.now().plusHours(24))
            .aisle("Aisle " + (random.nextInt(20) + 1));
        
        // Check for sale
        if (random.nextDouble() < SALE_PROBABILITY) {
            double saleDiscount = 0.1 + (0.3 * random.nextDouble()); // 10-40% off
            double salePrice = Math.round(regularPrice * (1 - saleDiscount) * 100.0) / 100.0;
            builder.salePrice(salePrice).onSale(true);
        } else {
            builder.onSale(false);
        }
        
        return builder.build();
    }
    
    private ProductPrice generateMockSearchResult(String searchTerm, int index) {
        return ProductPrice.builder()
            .sku("SEARCH-SKU-" + index)
            .productName(searchTerm + " Product " + (index + 1))
            .regularPrice(Math.round((2.99 + index * 1.5) * 100.0) / 100.0)
            .onSale(index % 3 == 0)
            .salePrice(index % 3 == 0 ? Math.round((2.99 + index * 1.5) * 0.8 * 100.0) / 100.0 : null)
            .inStock(true)
            .stockQuantity(50 + index * 10)
            .unit("each")
            .build();
    }
} 