package com.groceryautomation.graphql.resolver.query;

import com.groceryautomation.entity.Order;
import com.groceryautomation.enums.OrderStatus;
import com.groceryautomation.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderQueryResolver {
    
    private final OrderRepository orderRepository;
    
    @QueryMapping
    public Order order(@Argument Long id) {
        log.info("Fetching order with ID: {} from GraphQL", id);
        try {
            return orderRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Error fetching order {}: {}", id, e.getMessage());
            return null;
        }
    }
    
    @QueryMapping
    public List<Order> ordersByUser(@Argument Long userId, @Argument OrderStatus status) {
        log.info("Fetching orders for user: {} with status: {} from GraphQL", userId, status);
        try {
            if (status != null) {
                return orderRepository.findByUserIdAndStatus(userId, status);
            } else {
                return orderRepository.findByUserId(userId);
            }
        } catch (Exception e) {
            log.error("Error fetching orders for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }
    
    @QueryMapping
    public List<Order> draftOrders(@Argument Long userId) {
        log.info("Fetching draft orders for user: {} from GraphQL", userId);
        try {
            return orderRepository.findDraftOrdersByUserId(userId);
        } catch (Exception e) {
            log.error("Error fetching draft orders for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }
}