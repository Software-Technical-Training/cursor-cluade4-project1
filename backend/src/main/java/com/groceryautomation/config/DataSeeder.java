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

// @Component
@RequiredArgsConstructor
@Slf4j
@Profile("!production") // Don't run in production
public class DataSeeder implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final GroceryItemRepository groceryItemRepository;
    private final DeviceRepository deviceRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserStoreRepository userStoreRepository;
    private final OrderRepository orderRepository;
    
    @Override
    public void run(String... args) {
        log.info("Starting data seeding...");
        
        if (userRepository.count() > 0) {
            log.info("Data already exists, skipping seed");
            return;
        }
        
        // Create grocery items catalog (this is our product database)
        List<GroceryItem> groceryItems = createGroceryItems();
        
        // Simulate user registration flow:
        // 1. User registers with basic info and device
        User testUser = createTestUser();
        Device device = createDevice(testUser);
        
        // 2. User searches for nearby stores (simulating Google Maps API response)
        // 3. User selects one store from the results
        Store selectedStore = createSelectedStore();
        
        // 4. Create backup store (simulating user selecting a second store)
        Store backupStore = createBackupStore();
        
        // 5. Create UserStore relationships (primary and backup)
        createUserStoreRelationship(testUser, selectedStore, 1); // Primary
        createUserStoreRelationship(testUser, backupStore, 2);   // Backup
        
        // 6. Create initial inventory for the device
        createInventory(device, groceryItems);
        
        // 7. Create sample orders for testing
        createSampleOrders(testUser, selectedStore, groceryItems);
        
        log.info("Data seeding completed successfully!");
        log.info("Created 1 user with 1 device and 1 selected store (simulating real registration flow)");
    }
    
    private Store createSelectedStore() {
        // This simulates the user selecting ONE store from Google Maps API results
        // In real flow: Google Maps API -> User sees 5 stores -> Picks one -> We save only that one
        Store selectedStore = Store.builder()
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
                .build();
        
        Store savedStore = storeRepository.save(selectedStore);
        log.info("Created store '{}' that user selected from Google Maps results", savedStore.getName());
        return savedStore;
    }
    
    private Store createBackupStore() {
        // Simulate discovering a backup store via Google Maps API
        Store backupStore = Store.builder()
            .name("QuickShop Express")
            .address("567 Market Street, San Francisco, CA 94105")
            .latitude(37.7749)
            .longitude(-122.4194)
            .phone("(415) 555-0102")
            .email("info@quickshop.com")
            .openingTime(LocalTime.of(7, 0))
            .closingTime(LocalTime.of(23, 0))
            .active(true)
            .hasDelivery(true)
            .hasPickup(true)
            .minimumOrderAmount(15.0)
            .deliveryFee(4.99)
            .build();
        
        Store savedStore = storeRepository.save(backupStore);
        log.info("Created backup store '{}' that user selected as backup option", savedStore.getName());
        return savedStore;
    }
    
    private List<GroceryItem> createGroceryItems() {
        List<GroceryItem> items = Arrays.asList(
            // Dairy
            GroceryItem.builder()
                .name("Whole Milk")
                .category("Dairy")
                .unit("gallon")
                .barcode("123456789001")
                .sku("MILK-WH-GAL")
                .brand("Farm Fresh")
                .defaultThreshold(0.5)
                .build(),
                
            GroceryItem.builder()
                .name("Eggs")
                .category("Dairy")
                .unit("dozen")
                .barcode("123456789002")
                .sku("EGG-LG-DZ")
                .brand("Happy Hens")
                .defaultThreshold(0.5)
                .build(),
                
            GroceryItem.builder()
                .name("Greek Yogurt")
                .category("Dairy")
                .unit("container")
                .barcode("123456789003")
                .sku("YOG-GRK-32")
                .brand("Probiotic Plus")
                .defaultThreshold(2.0)
                .build(),
                
            // Produce
            GroceryItem.builder()
                .name("Bananas")
                .category("Produce")
                .unit("pound")
                .barcode("123456789004")
                .sku("BAN-YEL-LB")
                .brand("Tropical")
                .defaultThreshold(2.0)
                .build(),
                
            GroceryItem.builder()
                .name("Apples")
                .category("Produce")
                .unit("pound")
                .barcode("123456789005")
                .sku("APL-RED-LB")
                .brand("Orchard Fresh")
                .defaultThreshold(3.0)
                .build(),
                
            // Meat
            GroceryItem.builder()
                .name("Chicken Breast")
                .category("Meat")
                .unit("pound")
                .barcode("123456789006")
                .sku("CHK-BRS-LB")
                .brand("Free Range")
                .defaultThreshold(1.0)
                .build(),
                
            // Bakery
            GroceryItem.builder()
                .name("Whole Wheat Bread")
                .category("Bakery")
                .unit("loaf")
                .barcode("123456789007")
                .sku("BRD-WW-LF")
                .brand("Artisan Bakery")
                .defaultThreshold(1.0)
                .build(),
                
            // Beverages
            GroceryItem.builder()
                .name("Orange Juice")
                .category("Beverages")
                .unit("half gallon")
                .barcode("123456789008")
                .sku("JUC-ORG-HG")
                .brand("Sunny Grove")
                .defaultThreshold(1.0)
                .build(),
                
            GroceryItem.builder()
                .name("Sparkling Water")
                .category("Beverages")
                .unit("12-pack")
                .barcode("123456789009")
                .sku("WTR-SPK-12")
                .brand("Crystal Springs")
                .defaultThreshold(1.0)
                .build(),
                
            // Pantry
            GroceryItem.builder()
                .name("Pasta")
                .category("Pantry")
                .unit("box")
                .barcode("123456789010")
                .sku("PST-PEN-16")
                .brand("Italian Select")
                .defaultThreshold(2.0)
                .build()
        );
        
        return groceryItemRepository.saveAll(items);
    }
    

    private User createTestUser() {
        // Step 1: User registers with basic info (no store selected yet)
        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("Test@123") // In production, this would be hashed
                .phone("+1234567890")
                .address("123 Main Street, Apt 4B, San Francisco, CA 94105")
                .latitude(37.7749)
                .longitude(-122.4194)
                // Note: selectedStore is null at this point - will be set after user picks from Google Maps
                .active(true)
                .build();
                
        User savedUser = userRepository.save(user);
        log.info("Created user '{}' with location lat: {}, lon: {}", 
                savedUser.getEmail(), savedUser.getLatitude(), savedUser.getLongitude());
        return savedUser;
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
    
    private void createUserStoreRelationship(User user, Store store, Integer priority) {
        UserStore userStore = UserStore.builder()
                .user(user)
                .store(store)
                .priority(priority)
                .isActive(true)
                .maxDeliveryFee(10.00) // User willing to pay up to $10 for delivery
                .maxDistanceMiles(5.0) // User willing to order from stores within 5 miles
                .notes("Primary store selected during registration")
                .build();
        
        userStoreRepository.save(userStore);
        log.info("Created user-store relationship: User '{}' selected '{}' as priority {} store", 
                user.getEmail(), store.getName(), priority);
    }
    
    private void createSampleOrders(User user, Store store, List<GroceryItem> groceryItems) {
        // Create a past order (DELIVERED)
        Order pastOrder = createPastOrder(user, store, groceryItems);
        
        // Create a draft order (DRAFT) - awaiting user approval
        Order draftOrder = createDraftOrder(user, store, groceryItems);
        
        log.info("Created {} sample orders for user '{}': 1 past order, 1 draft order", 
                2, user.getEmail());
    }
    
    private Order createPastOrder(User user, Store store, List<GroceryItem> groceryItems) {
        Order order = Order.builder()
                .orderNumber("ORD-" + System.currentTimeMillis())
                .user(user)
                .store(store)
                .status(com.groceryautomation.enums.OrderStatus.DELIVERED)
                .subtotal(45.67)
                .deliveryFee(5.99)
                .tax(3.65)
                .totalAmount(55.31)
                .estimatedTotal(55.31)
                .finalTotal(55.31)
                .deliveryAddress(user.getAddress())
                .scheduledDeliveryTime(LocalDateTime.now().minusDays(2).withHour(14).withMinute(0))
                .actualDeliveryTime(LocalDateTime.now().minusDays(2).withHour(14).withMinute(30))
                .submittedAt(LocalDateTime.now().minusDays(3))
                .externalOrderId("EXT-" + store.getId() + "-" + (System.currentTimeMillis() - 259200000))
                .notificationSent(true)
                .trackingNumber("TRK123456789")
                .deliveryPersonName("Mike Johnson")
                .deliveryPersonPhone("+1-555-0123")
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Add order items for the past order
        List<OrderItem> pastOrderItems = Arrays.asList(
            OrderItem.builder()
                .order(savedOrder)
                .groceryItem(groceryItems.get(0)) // Milk
                .quantity(1.0)
                .price(4.99)
                .priceAtCreation(4.99)
                .currentPrice(4.99)
                .originalQuantity(1.0)
                .notes("Fresh organic milk")
                .build(),
                
            OrderItem.builder()
                .order(savedOrder)
                .groceryItem(groceryItems.get(1)) // Eggs
                .quantity(2.0)
                .price(5.99)
                .priceAtCreation(5.99)
                .currentPrice(5.99)
                .originalQuantity(2.0)
                .build(),
                
            OrderItem.builder()
                .order(savedOrder)
                .groceryItem(groceryItems.get(3)) // Bananas
                .quantity(3.0)
                .price(0.59)
                .priceAtCreation(0.59)
                .currentPrice(0.59)
                .originalQuantity(3.0)
                .build(),
                
            OrderItem.builder()
                .order(savedOrder)
                .groceryItem(groceryItems.get(7)) // Orange Juice
                .quantity(1.0)
                .price(4.49)
                .priceAtCreation(4.49)
                .currentPrice(4.49)
                .originalQuantity(1.0)
                .build()
        );
        
        savedOrder.getItems().addAll(pastOrderItems);
        // Need to save the order again after adding items to persist them
        savedOrder = orderRepository.save(savedOrder);
        
        log.info("Created past order '{}' for user '{}' with {} items - Status: DELIVERED", 
                savedOrder.getOrderNumber(), user.getEmail(), savedOrder.getItems().size());
        return savedOrder;
    }
    
    private Order createDraftOrder(User user, Store store, List<GroceryItem> groceryItems) {
        Order order = Order.builder()
                .orderNumber("DRAFT-" + System.currentTimeMillis())
                .user(user)
                .store(store)
                .status(com.groceryautomation.enums.OrderStatus.DRAFT)
                .subtotal(23.45)
                .deliveryFee(5.99)
                .tax(1.88)
                .totalAmount(31.32)
                .estimatedTotal(31.32) // Initially estimated
                .draftCreatedAt(LocalDateTime.now().minusHours(2))
                .deliveryAddress(user.getAddress())
                .notificationSent(true)
                .deliveryInstructions("Please leave at front door if no answer")
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Add order items for the draft order (low inventory items that need approval)
        List<OrderItem> draftOrderItems = Arrays.asList(
            OrderItem.builder()
                .order(savedOrder)
                .groceryItem(groceryItems.get(0)) // Milk
                .quantity(1.0)
                .price(5.29) // Current price
                .priceAtCreation(4.99) // Price when draft was created
                .currentPrice(5.29) // Latest price (showing price change)
                .priceChanged(true) // Price increased since draft creation
                .originalQuantity(1.0)
                .build(),
                
            OrderItem.builder()
                .order(savedOrder)
                .groceryItem(groceryItems.get(1)) // Eggs
                .quantity(1.0)
                .price(5.99)
                .priceAtCreation(5.99)
                .currentPrice(5.99)
                .originalQuantity(2.0) // User can modify from original suggestion
                .quantityModified(true)
                .build(),
                
            OrderItem.builder()
                .order(savedOrder)
                .groceryItem(groceryItems.get(5)) // Chicken
                .quantity(2.0)
                .price(8.99)
                .priceAtCreation(8.99)
                .currentPrice(8.99)
                .originalQuantity(2.0)
                .build()
        );
        
        savedOrder.getItems().addAll(draftOrderItems);
        // Need to save the order again after adding items to persist them
        savedOrder = orderRepository.save(savedOrder);
        
        log.info("Created draft order '{}' for user '{}' with {} items - Status: DRAFT (awaiting approval)", 
                savedOrder.getOrderNumber(), user.getEmail(), savedOrder.getItems().size());
        return savedOrder;
    }
} 