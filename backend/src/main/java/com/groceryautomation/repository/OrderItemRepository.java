package com.groceryautomation.repository;

import com.groceryautomation.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.groceryItem.id = :itemId")
    List<OrderItem> findByGroceryItemId(@Param("itemId") Long itemId);
    
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.groceryItem.id = :itemId")
    Double getTotalQuantityOrderedForItem(@Param("itemId") Long itemId);
} 