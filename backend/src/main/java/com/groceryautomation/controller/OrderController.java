package com.groceryautomation.controller;

import com.groceryautomation.entity.Order;
import com.groceryautomation.enums.OrderStatus;
import com.groceryautomation.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders and viewing order history")
public class OrderController {
    
    private final OrderRepository orderRepository;
    
    @GetMapping("/drafts/user/{userId}")
    @Operation(summary = "Get draft orders for user", 
               description = "Retrieve all draft/pending orders that require user approval")
    public ResponseEntity<List<Order>> getDraftOrdersForUser(
            @Parameter(description = "User ID", example = "1") 
            @PathVariable Long userId) {
        
        log.info("Fetching draft orders for user: {}", userId);
        List<Order> draftOrders = orderRepository.findDraftOrdersByUserId(userId);
        
        log.info("Found {} draft orders for user {}", draftOrders.size(), userId);
        return ResponseEntity.ok(draftOrders);
    }
    
    @GetMapping("/history/user/{userId}")
    @Operation(summary = "Get order history for user", 
               description = "Retrieve all completed/past orders for a user")
    public ResponseEntity<List<Order>> getOrderHistoryForUser(
            @Parameter(description = "User ID", example = "1") 
            @PathVariable Long userId) {
        
        log.info("Fetching order history for user: {}", userId);
        List<Order> orderHistory = orderRepository.findOrderHistoryByUserId(userId);
        
        log.info("Found {} orders in history for user {}", orderHistory.size(), userId);
        return ResponseEntity.ok(orderHistory);
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details", 
               description = "Retrieve detailed information about a specific order")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "Order ID", example = "1") 
            @PathVariable Long orderId) {
        
        log.info("Fetching order details for order: {}", orderId);
        Optional<Order> order = orderRepository.findById(orderId);
        
        if (order.isPresent()) {
            log.info("Found order '{}' with status: {}", 
                    order.get().getOrderNumber(), order.get().getStatus());
            return ResponseEntity.ok(order.get());
        } else {
            log.warn("Order not found: {}", orderId);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all orders for user", 
               description = "Retrieve all orders (draft and completed) for a user")
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @Parameter(description = "User ID", example = "1") 
            @PathVariable Long userId,
            @Parameter(description = "Filter by status (optional)", example = "DRAFT") 
            @RequestParam(required = false) String status) {
        
        log.info("Fetching all orders for user: {} with status filter: {}", userId, status);
        
        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByUserIdAndStatus(userId, orderStatus);
                log.info("Found {} orders with status {} for user {}", orders.size(), status, userId);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid order status: {}", status);
                return ResponseEntity.badRequest().build();
            }
        } else {
            orders = orderRepository.findByUserId(userId);
            log.info("Found {} total orders for user {}", orders.size(), userId);
        }
        
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/count/drafts/user/{userId}")
    @Operation(summary = "Get draft order count", 
               description = "Get count of pending orders that need user approval")
    public ResponseEntity<Long> getDraftOrderCount(
            @Parameter(description = "User ID", example = "1") 
            @PathVariable Long userId) {
        
        log.info("Counting draft orders for user: {}", userId);
        Long count = orderRepository.countDraftOrdersByUserId(userId);
        
        log.info("User {} has {} draft orders pending approval", userId, count);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/{orderId}/approve")
    @Operation(summary = "Approve draft order", 
               description = "User approves a draft order for submission to store")
    public ResponseEntity<Order> approveDraftOrder(
            @Parameter(description = "Order ID", example = "1") 
            @PathVariable Long orderId) {
        
        log.info("Approving draft order: {}", orderId);
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            log.warn("Order not found: {}", orderId);
            return ResponseEntity.notFound().build();
        }
        
        Order order = orderOpt.get();
        
        if (!order.getStatus().isModifiable()) {
            log.warn("Order {} cannot be approved - current status: {}", orderId, order.getStatus());
            return ResponseEntity.badRequest().build();
        }
        
        // Update order status and timestamps
        order.setStatus(OrderStatus.SUBMITTED);
        order.setUserReviewedAt(java.time.LocalDateTime.now());
        order.setSubmittedAt(java.time.LocalDateTime.now());
        order.setFinalTotal(order.getTotalAmount());
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("Order '{}' approved and submitted with total: ${}", 
                savedOrder.getOrderNumber(), savedOrder.getFinalTotal());
        return ResponseEntity.ok(savedOrder);
    }
    
    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel draft order", 
               description = "User cancels a draft order before submission")
    public ResponseEntity<Void> cancelDraftOrder(
            @Parameter(description = "Order ID", example = "1") 
            @PathVariable Long orderId) {
        
        log.info("Cancelling draft order: {}", orderId);
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (orderOpt.isEmpty()) {
            log.warn("Order not found: {}", orderId);
            return ResponseEntity.notFound().build();
        }
        
        Order order = orderOpt.get();
        
        if (!order.getStatus().isModifiable()) {
            log.warn("Order {} cannot be cancelled - current status: {}", orderId, order.getStatus());
            return ResponseEntity.badRequest().build();
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        log.info("Draft order '{}' cancelled successfully", order.getOrderNumber());
        return ResponseEntity.noContent().build();
    }
} 