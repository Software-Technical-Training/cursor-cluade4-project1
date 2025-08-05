package com.groceryautomation.graphql.resolver.field;

import com.groceryautomation.entity.Device;
import com.groceryautomation.entity.Order;
import com.groceryautomation.entity.User;
import com.groceryautomation.entity.UserStore;
import com.groceryautomation.enums.OrderStatus;
import com.groceryautomation.repository.DeviceRepository;
import com.groceryautomation.repository.OrderRepository;
import com.groceryautomation.repository.UserStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserFieldResolver {
    
    private final DeviceRepository deviceRepository;
    private final UserStoreRepository userStoreRepository;
    private final OrderRepository orderRepository;
    
    @SchemaMapping(typeName = "User", field = "devices")
    public List<Device> getDevices(User user) {
        log.debug("Fetching devices for user: {}", user.getId());
        return deviceRepository.findByUserId(user.getId());
    }
    
    @SchemaMapping(typeName = "User", field = "stores")
    public List<UserStore> getStores(User user) {
        log.debug("Fetching stores for user: {}", user.getId());
        return userStoreRepository.findByUserIdOrderByPriorityAsc(user.getId());
    }
    
    @SchemaMapping(typeName = "User", field = "orders")
    public List<Order> getOrders(User user, @Argument OrderStatus status) {
        log.debug("Fetching orders for user: {} with status: {}", user.getId(), status);
        if (status != null) {
            return orderRepository.findByUserIdAndStatus(user.getId(), status);
        }
        return orderRepository.findByUserId(user.getId());
    }
}