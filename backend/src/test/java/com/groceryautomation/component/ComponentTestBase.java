package com.groceryautomation.component;

import com.groceryautomation.dto.ProductPrice;
import com.groceryautomation.entity.*;
import com.groceryautomation.enums.InventoryStatus;
import com.groceryautomation.enums.OrderStatus;
import com.groceryautomation.repository.*;
import com.groceryautomation.service.StoreApiService;
import com.groceryautomation.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Base class for component tests that test workflows across multiple classes.
 * Provides common test infrastructure, Spring Boot configuration, and test utilities.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:componenttestdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.com.groceryautomation=DEBUG",
    "spring.jpa.show-sql=false"
})
@Transactional
public abstract class ComponentTestBase {

    // Repositories for test data setup
    @Autowired
    protected UserRepository userRepository;
    
    @Autowired
    protected DeviceRepository deviceRepository;
    
    @Autowired
    protected StoreRepository storeRepository;
    
    @Autowired
    protected UserStoreRepository userStoreRepository;
    
    @Autowired
    protected GroceryItemRepository groceryItemRepository;
    
    @Autowired
    protected InventoryItemRepository inventoryItemRepository;
    
    @Autowired
    protected OrderRepository orderRepository;
    
    @Autowired
    protected OrderItemRepository orderItemRepository;
    
    @Autowired
    protected NotificationRepository notificationRepository;

    // Mock external dependencies
    @MockBean
    protected StoreApiService storeApiService;
    
    @MockBean
    protected NotificationService notificationService;

    // Test utilities
    @Autowired
    protected ObjectMapper objectMapper;

    // Test data
    protected User testUser;
    protected Device testDevice;
    protected Store primaryStore;
    protected Store backupStore;
    protected GroceryItem milkItem;
    protected GroceryItem breadItem;
    protected GroceryItem eggsItem;

    @BeforeEach
    void setUpComponentTestBase() {
        // Create test data (reuse existing data from DataSeeder where possible)
        createTestUser();
        createTestStores();
        loadExistingGroceryItems(); // Use existing items from DataSeeder
        createTestDevice();
        setupMockServices();
    }

    /**
     * Create a test user with all required fields
     */
    protected void createTestUser() {
        testUser = User.builder()
                .name("Component Test User")
                .email("component.test@example.com")
                .password("hashedPassword123")
                .phone("+15551234567")
                .address("123 Main St, Anytown, CA 90210")
                .latitude(37.7749)
                .longitude(-122.4194)
                .active(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    /**
     * Create test stores (primary and backup)
     */
    protected void createTestStores() {
        primaryStore = Store.builder()
                .name("Fresh Mart")
                .address("456 Market St, Anytown, CA 90210")
                .latitude(37.7849)
                .longitude(-122.4094)
                .phone("+15559876543")
                .email("orders@freshmart.com")
                .googlePlaceId("ChIJTest123Primary")
                .active(true)
                .acceptingOrders(true)
                .hasDelivery(true)
                .hasPickup(true)
                .deliveryFee(4.99)
                .minimumOrderAmount(35.00)
                .build();
        primaryStore = storeRepository.save(primaryStore);

        backupStore = Store.builder()
                .name("Quick Shop")
                .address("789 Oak Ave, Anytown, CA 90210")
                .latitude(37.7649)
                .longitude(-122.4294)
                .phone("+15555551234")
                .email("orders@quickshop.com")
                .googlePlaceId("ChIJTest456Backup")
                .active(true)
                .acceptingOrders(true)
                .hasDelivery(true)
                .hasPickup(false)
                .deliveryFee(5.99)
                .minimumOrderAmount(25.00)
                .build();
        backupStore = storeRepository.save(backupStore);
    }

    /**
     * Load existing grocery items from DataSeeder instead of creating duplicates
     */
    protected void loadExistingGroceryItems() {
        // Load existing items created by DataSeeder using barcode
        milkItem = groceryItemRepository.findByBarcode("123456789001").orElse(null);
        breadItem = groceryItemRepository.findByBarcode("123456789002").orElse(null);
        eggsItem = groceryItemRepository.findByBarcode("123456789003").orElse(null);
        
        // If items don't exist (shouldn't happen with DataSeeder), create unique ones
        if (milkItem == null) {
            milkItem = GroceryItem.builder()
                    .name("Component Test Milk")
                    .category("Dairy")
                    .unit("gallon")
                    .barcode("COMP-123456789001")
                    .sku("COMP-MILK-WHOLE-1GAL")
                    .brand("Test Farms")
                    .description("Test whole milk, 1 gallon")
                    .defaultThreshold(0.25)
                    .active(true)
                    .build();
            milkItem = groceryItemRepository.save(milkItem);
        }
        
        if (breadItem == null) {
            breadItem = GroceryItem.builder()
                    .name("Component Test Bread")
                    .category("Bakery")
                    .unit("loaf")
                    .barcode("COMP-123456789002")
                    .sku("COMP-BREAD-WW-LOAF")
                    .brand("Test Bakery")
                    .description("Test whole wheat bread loaf")
                    .defaultThreshold(0.5)
                    .active(true)
                    .build();
            breadItem = groceryItemRepository.save(breadItem);
        }
        
        if (eggsItem == null) {
            eggsItem = GroceryItem.builder()
                    .name("Component Test Eggs")
                    .category("Dairy")
                    .unit("dozen")
                    .barcode("COMP-123456789003")
                    .sku("COMP-EGGS-LG-DOZ")
                    .brand("Test Farm")
                    .description("Test large eggs, 12 count")
                    .defaultThreshold(0.25)
                    .active(true)
                    .build();
            eggsItem = groceryItemRepository.save(eggsItem);
        }
    }

    /**
     * Create test device and associate with user
     */
    protected void createTestDevice() {
        testDevice = Device.builder()
                .deviceId("COMPONENT-FRIDGE-001")
                .user(testUser)
                .name("Component Test Smart Fridge")
                .online(true)
                .lastSync(LocalDateTime.now())
                .build();
        testDevice = deviceRepository.save(testDevice);
    }

    /**
     * Setup mock services with default responses
     */
    protected void setupMockServices() {
        // Mock store API service responses
        Map<String, ProductPrice> mockPrices = new HashMap<>();
        mockPrices.put("MILK-WHOLE-1GAL", ProductPrice.builder()
                .sku("MILK-WHOLE-1GAL")
                .productName("Whole Milk")
                .regularPrice(3.99)
                .inStock(true)
                .build());
        mockPrices.put("BREAD-WW-LOAF", ProductPrice.builder()
                .sku("BREAD-WW-LOAF")
                .productName("Whole Wheat Bread")
                .regularPrice(2.49)
                .inStock(true)
                .build());
        mockPrices.put("EGGS-LG-DOZ", ProductPrice.builder()
                .sku("EGGS-LG-DOZ")
                .productName("Large Eggs")
                .regularPrice(4.29)
                .inStock(true)
                .build());

        when(storeApiService.fetchPrices(any(), anyList())).thenReturn(mockPrices);
        when(storeApiService.isStoreApiAvailable(any())).thenReturn(true);
        when(storeApiService.submitOrder(any())).thenReturn("EXT-ORDER-123");
        when(storeApiService.checkOrderStatus(any(), any())).thenReturn("CONFIRMED");
    }

    /**
     * Create user-store relationship
     */
    protected UserStore createUserStoreRelationship(User user, Store store, Integer priority) {
        UserStore userStore = UserStore.builder()
                .user(user)
                .store(store)
                .priority(priority)
                .isActive(true)
                .maxDeliveryFee(10.0)
                .maxDistanceMiles(15.0)
                .build();
        return userStoreRepository.save(userStore);
    }

    /**
     * Create inventory item for testing
     */
    protected InventoryItem createInventoryItem(Device device, GroceryItem groceryItem, Double currentAmount, Double threshold) {
        InventoryItem inventoryItem = InventoryItem.builder()
                .device(device)
                .groceryItem(groceryItem)
                .quantity(currentAmount)
                .thresholdQuantity(threshold != null ? threshold : groceryItem.getDefaultThreshold())
                .status(determineInventoryStatus(currentAmount, threshold != null ? threshold : groceryItem.getDefaultThreshold()))
                .build();
        return inventoryItemRepository.save(inventoryItem);
    }

    /**
     * Create test order
     */
    protected Order createTestOrder(User user, Store store, OrderStatus status) {
        Order order = Order.builder()
                .user(user)
                .store(store)
                .status(status)
                .orderNumber("ORDER-" + System.currentTimeMillis())
                .totalAmount(0.0)
                .deliveryFee(store.getDeliveryFee())
                .build();
        return orderRepository.save(order);
    }

    /**
     * Helper method to determine inventory status
     */
    private InventoryStatus determineInventoryStatus(Double currentAmount, Double threshold) {
        if (currentAmount <= 0) {
            return InventoryStatus.OUT_OF_STOCK;
        } else if (currentAmount <= threshold * 0.5) {
            return InventoryStatus.CRITICAL;
        } else if (currentAmount <= threshold) {
            return InventoryStatus.LOW;
        } else {
            return InventoryStatus.SUFFICIENT;
        }
    }

    /**
     * Clean up test data (called automatically due to @Transactional)
     */
    protected void cleanupTestData() {
        // Spring's @Transactional will automatically rollback changes
        // This method can be overridden for custom cleanup if needed
    }
} 