package com.groceryautomation.graphql.resolver.field;

import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.entity.User;
import com.groceryautomation.repository.InventoryItemRepository;
import com.groceryautomation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DeviceFieldResolver {
    
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;
    
    @SchemaMapping(typeName = "Device", field = "user")
    public User getUser(Device device) {
        log.debug("Fetching user for device: {}", device.getId());
        return userRepository.findById(device.getUser().getId()).orElse(null);
    }
    
    @SchemaMapping(typeName = "Device", field = "inventoryItems")
    public List<InventoryItem> getInventoryItems(Device device) {
        log.debug("Fetching inventory items for device: {}", device.getId());
        return inventoryItemRepository.findByDeviceId(device.getId());
    }
    
    @SchemaMapping(typeName = "Device", field = "inventoryCount")
    public Integer getInventoryCount(Device device) {
        log.debug("Counting inventory items for device: {}", device.getId());
        return inventoryItemRepository.countByDeviceId(device.getId());
    }
}