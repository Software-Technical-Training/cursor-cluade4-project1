package com.groceryautomation.service;

import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.repository.DeviceRepository;
import com.groceryautomation.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockSensorService {
    
    private final DeviceRepository deviceRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final Random random = new Random();
    
    // @Scheduled(fixedDelay = 30000, initialDelay = 60000) // Run every 30 seconds, start after 1 minute
    // @Transactional
    public void simulateInventoryConsumption() {
        log.debug("Running mock sensor data generation...");
        
        // Get all active and online devices
        List<Device> activeDevices = deviceRepository.findAllActiveAndOnlineDevices();
        
        for (Device device : activeDevices) {
            simulateDeviceConsumption(device);
        }
    }
    
    private void simulateDeviceConsumption(Device device) {
        List<InventoryItem> items = inventoryItemRepository.findByDeviceId(device.getId());
        
        for (InventoryItem item : items) {
            // Skip items that are already out of stock
            if (item.getQuantity() <= 0) {
                continue;
            }
            
            // Random chance of consumption (70% chance)
            if (random.nextDouble() < 0.7) {
                // Calculate consumption based on device's mock consumption rate
                double consumptionRate = device.getMockConsumptionRate();
                double baseConsumption = item.getQuantity() * consumptionRate;
                
                // Add some randomness (±20%)
                double randomFactor = 0.8 + (random.nextDouble() * 0.4);
                double consumption = baseConsumption * randomFactor;
                
                // Ensure we don't go negative
                double newQuantity = Math.max(0, item.getQuantity() - consumption);
                item.setQuantity(newQuantity);
                
                // The @PreUpdate will automatically update the status
                inventoryItemRepository.save(item);
                
                log.debug("Consumed {:.2f} {} of {} (device: {})", 
                    consumption, item.getGroceryItem().getUnit(), 
                    item.getGroceryItem().getName(), device.getDeviceId());
            }
        }
        
        // Update device last sync time
        device.setLastSync(LocalDateTime.now());
        deviceRepository.save(device);
        
        log.info("Mock sensor data updated for device: {}", device.getDeviceId());
    }
    
    // @Scheduled(fixedDelay = 300000, initialDelay = 120000) // Run every 5 minutes, start after 2 minutes
    // @Transactional
    public void simulateOccasionalRestocking() {
        log.debug("Running occasional restocking simulation...");
        
        List<Device> activeDevices = deviceRepository.findAllActiveAndOnlineDevices();
        
        for (Device device : activeDevices) {
            // 20% chance of restocking
            if (random.nextDouble() < 0.2) {
                restockRandomItem(device);
            }
        }
    }
    
    private void restockRandomItem(Device device) {
        List<InventoryItem> items = inventoryItemRepository.findByDeviceId(device.getId());
        
        if (!items.isEmpty()) {
            // Pick a random item to restock
            InventoryItem item = items.get(random.nextInt(items.size()));
            
            // Restock to a random amount between 80% and 150% of threshold
            double restockAmount = item.getThresholdQuantity() * (0.8 + random.nextDouble() * 0.7);
            item.setQuantity(item.getQuantity() + restockAmount);
            
            inventoryItemRepository.save(item);
            
            log.info("Restocked {} {} of {} (device: {})", 
                restockAmount, item.getGroceryItem().getUnit(),
                item.getGroceryItem().getName(), device.getDeviceId());
        }
    }
} 