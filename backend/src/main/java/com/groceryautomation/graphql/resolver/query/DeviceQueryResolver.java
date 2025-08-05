package com.groceryautomation.graphql.resolver.query;

import com.groceryautomation.entity.Device;
import com.groceryautomation.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DeviceQueryResolver {
    
    private final DeviceService deviceService;
    
    @QueryMapping
    public Device device(@Argument Long id) {
        log.info("Fetching device with ID: {} from GraphQL", id);
        try {
            return deviceService.getDeviceById(id);
        } catch (Exception e) {
            log.error("Error fetching device {}: {}", id, e.getMessage());
            return null;
        }
    }
    
    @QueryMapping
    public List<Device> devicesByUser(@Argument Long userId) {
        log.info("Fetching devices for user: {} from GraphQL", userId);
        try {
            return deviceService.getDevicesByUserId(userId);
        } catch (Exception e) {
            log.error("Error fetching devices for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }
}