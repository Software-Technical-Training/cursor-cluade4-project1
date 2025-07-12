package com.groceryautomation.config;

import com.groceryautomation.entity.*;
import com.groceryautomation.enums.InventoryStatus;
import com.groceryautomation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!production") // Don't run in production
public class DataSeeder implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final GroceryItemRepository groceryItemRepository;
    private final DeviceRepository deviceRepository;
    private final InventoryItemRepository inventoryItemRepository;
    
    @Override
    public void run(String... args) {
        log.info("Starting data seeding...");
        
        if (userRepository.count() > 0) {
            log.info("Data already exists, skipping seed");
            return;
        }
        
        // Create test stores
        List<Store> stores = createStores();
        
        // Create grocery items catalog
        List<GroceryItem> groceryItems = createGroceryItems();
        
        // Create test user
        User testUser = createTestUser(stores.get(0));
        
        // Create device and inventory for test user
        Device device = createDevice(testUser);
        createInventory(device, groceryItems);
        
        log.info("Data seeding completed successfully!");
    }
    
    private List<Store> createStores() {
        List<Store> stores = Arrays.asList(
            Store.builder()
                .name("Fresh Mart Downtown")
                .address("399 4th Street, San Francisco, CA 94107")
                .latitude(37.7816)
                .longitude(-122.3988)
                .phone("(415) 555-0101")
                .email("downtown@freshmart.com")
                .openingTime(LocalTime.of(7, 0))
                .closingTime(LocalTime.of(22, 0))
                .hasDelivery(true)
                .hasPickup(true)
                .deliveryFee(5.99)
                .minimumOrderAmount(25.00)
                .build(),
                
            Store.builder()
                .name("Organic Grocer")
                .address("555 9th Street, San Francisco, CA 94103")
                .latitude(37.7702)
                .longitude(-122.4142)
                .phone("(415) 555-0102")
                .email("info@organicgrocer.com")
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(21, 0))
                .hasDelivery(true)
                .hasPickup(true)
                .deliveryFee(7.99)
                .minimumOrderAmount(35.00)
                .build(),
                
            Store.builder()
                .name("QuickStop Market")
                .address("145 Jackson Street, San Francisco, CA 94111")
                .latitude(37.7966)
                .longitude(-122.3988)
                .phone("(415) 555-0103")
                .email("service@quickstop.com")
                .openingTime(LocalTime.of(6, 0))
                .closingTime(LocalTime.of(23, 0))
                .hasDelivery(true)
                .hasPickup(false)
                .deliveryFee(4.99)
                .minimumOrderAmount(20.00)
                .build()
        );
        
        return storeRepository.saveAll(stores);
    }
    
    private List<GroceryItem> createGroceryItems() {
        List<GroceryItem> items = Arrays.asList(
            // Dairy
            GroceryItem.builder()
                .name("Whole Milk")
                .category("Dairy")
                .unit("gallon")
                .barcode("123456789001")
                .brand("Farm Fresh")
                .price(4.99)
                .defaultThreshold(0.5)
                .build(),
                
            GroceryItem.builder()
                .name("Eggs")
                .category("Dairy")
                .unit("dozen")
                .barcode("123456789002")
                .brand("Happy Hens")
                .price(5.99)
                .defaultThreshold(0.5)
                .build(),
                
            GroceryItem.builder()
                .name("Greek Yogurt")
                .category("Dairy")
                .unit("container")
                .barcode("123456789003")
                .brand("Probiotic Plus")
                .price(4.49)
                .defaultThreshold(2.0)
                .build(),
                
            // Produce
            GroceryItem.builder()
                .name("Bananas")
                .category("Produce")
                .unit("pound")
                .barcode("123456789004")
                .brand("Tropical")
                .price(0.59)
                .defaultThreshold(2.0)
                .build(),
                
            GroceryItem.builder()
                .name("Apples")
                .category("Produce")
                .unit("pound")
                .barcode("123456789005")
                .brand("Orchard Fresh")
                .price(1.99)
                .defaultThreshold(3.0)
                .build(),
                
            // Meat
            GroceryItem.builder()
                .name("Chicken Breast")
                .category("Meat")
                .unit("pound")
                .barcode("123456789006")
                .brand("Free Range")
                .price(8.99)
                .defaultThreshold(1.0)
                .build(),
                
            // Bakery
            GroceryItem.builder()
                .name("Whole Wheat Bread")
                .category("Bakery")
                .unit("loaf")
                .barcode("123456789007")
                .brand("Artisan Bakery")
                .price(3.99)
                .defaultThreshold(1.0)
                .build(),
                
            // Beverages
            GroceryItem.builder()
                .name("Orange Juice")
                .category("Beverages")
                .unit("half gallon")
                .barcode("123456789008")
                .brand("Sunny Grove")
                .price(4.49)
                .defaultThreshold(1.0)
                .build(),
                
            GroceryItem.builder()
                .name("Sparkling Water")
                .category("Beverages")
                .unit("12-pack")
                .barcode("123456789009")
                .brand("Crystal Springs")
                .price(5.99)
                .defaultThreshold(1.0)
                .build(),
                
            // Pantry
            GroceryItem.builder()
                .name("Pasta")
                .category("Pantry")
                .unit("box")
                .barcode("123456789010")
                .brand("Italian Select")
                .price(2.49)
                .defaultThreshold(2.0)
                .build()
        );
        
        return groceryItemRepository.saveAll(items);
    }
    
    private User createTestUser(Store defaultStore) {
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Test@123") // In production, this would be hashed
                .phone("+1234567890")
                .address("123 Main Street, Apt 4B, San Francisco, CA 94105")
                .latitude(37.7749)
                .longitude(-122.4194)
                .selectedStore(defaultStore)
                .active(true)
                .build();
                
        return userRepository.save(user);
    }
    
    private Device createDevice(User user) {
        Device device = Device.builder()
                .deviceId("FRIDGE-001")
                .name("Kitchen Smart Fridge")
                .user(user)
                .online(true)
                .lastSync(LocalDateTime.now())
                .mockDataIntervalSeconds(30) // Generate mock data every 30 seconds
                .mockConsumptionRate(0.05) // Consume 5% of items per interval
                .build();
                
        return deviceRepository.save(device);
    }
    
    private void createInventory(Device device, List<GroceryItem> groceryItems) {
        // Create inventory items with various quantities to show different statuses
        List<InventoryItem> inventoryItems = Arrays.asList(
            // Low on milk
            InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItems.get(0)) // Milk
                .quantity(0.3)
                .thresholdQuantity(0.5)
                .addedAt(LocalDateTime.now().minusDays(5))
                .build(),
                
            // Out of eggs
            InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItems.get(1)) // Eggs
                .quantity(0.0)
                .thresholdQuantity(0.5)
                .addedAt(LocalDateTime.now().minusDays(7))
                .build(),
                
            // Good on yogurt
            InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItems.get(2)) // Yogurt
                .quantity(4.0)
                .thresholdQuantity(2.0)
                .addedAt(LocalDateTime.now().minusDays(2))
                .build(),
                
            // Critical on bananas
            InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItems.get(3)) // Bananas
                .quantity(0.5)
                .thresholdQuantity(2.0)
                .addedAt(LocalDateTime.now().minusDays(3))
                .build(),
                
            // Good on apples
            InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItems.get(4)) // Apples
                .quantity(5.0)
                .thresholdQuantity(3.0)
                .addedAt(LocalDateTime.now().minusDays(1))
                .build(),
                
            // Low on chicken
            InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItems.get(5)) // Chicken
                .quantity(0.8)
                .thresholdQuantity(1.0)
                .addedAt(LocalDateTime.now().minusDays(2))
                .build(),
                
            // Good on bread
            InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItems.get(6)) // Bread
                .quantity(2.0)
                .thresholdQuantity(1.0)
                .addedAt(LocalDateTime.now().minusDays(1))
                .build(),
                
            // Low on orange juice
            InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItems.get(7)) // OJ
                .quantity(0.4)
                .thresholdQuantity(1.0)
                .addedAt(LocalDateTime.now().minusDays(4))
                .build()
        );
        
        // Save all and let @PrePersist calculate status
        inventoryItemRepository.saveAll(inventoryItems);
        
        log.info("Created {} inventory items for device {}", inventoryItems.size(), device.getDeviceId());
    }
} 