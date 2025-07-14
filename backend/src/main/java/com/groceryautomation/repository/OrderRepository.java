package com.groceryautomation.repository;

import com.groceryautomation.entity.Order;
import com.groceryautomation.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByUserId(Long userId);
    
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    List<Order> findByStoreId(Long storeId);
    
    Page<Order> findByStoreIdAndStatus(Long storeId, OrderStatus status, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);
    
    boolean existsByOrderNumber(String orderNumber);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status IN ('DRAFT', 'USER_MODIFIED') ORDER BY o.createdAt DESC")
    List<Order> findDraftOrdersByUserId(@Param("userId") Long userId);
    
    @Query("SELECT o FROM Order o WHERE o.status = 'DRAFT' AND o.notificationSent = false")
    List<Order> findUnnotifiedDraftOrders();
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status NOT IN ('DRAFT', 'USER_MODIFIED') ORDER BY o.createdAt DESC")
    List<Order> findOrderHistoryByUserId(@Param("userId") Long userId);
    
    @Query("SELECT o FROM Order o WHERE o.externalOrderId = :externalOrderId")
    Optional<Order> findByExternalOrderId(@Param("externalOrderId") String externalOrderId);
    
    @Query("SELECT o FROM Order o WHERE o.status = 'SUBMITTED' AND o.updatedAt < :cutoffTime")
    List<Order> findStaleSubmittedOrders(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status IN ('DRAFT', 'USER_MODIFIED')")
    Long countDraftOrdersByUserId(@Param("userId") Long userId);
} 