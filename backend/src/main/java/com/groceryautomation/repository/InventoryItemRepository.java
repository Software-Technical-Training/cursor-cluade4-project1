package com.groceryautomation.repository;

import com.groceryautomation.entity.InventoryItem;
import com.groceryautomation.enums.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    List<InventoryItem> findByDeviceId(Long deviceId);
    
    List<InventoryItem> findByDeviceIdAndStatus(Long deviceId, InventoryStatus status);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.device.id = :deviceId AND i.status IN :statuses")
    List<InventoryItem> findByDeviceIdAndStatusIn(@Param("deviceId") Long deviceId, 
                                                  @Param("statuses") List<InventoryStatus> statuses);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.device.user.id = :userId")
    List<InventoryItem> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.device.user.id = :userId AND i.status IN (:statuses)")
    List<InventoryItem> findByUserIdAndStatusIn(@Param("userId") Long userId, 
                                                @Param("statuses") List<InventoryStatus> statuses);
    
    Optional<InventoryItem> findByDeviceIdAndGroceryItemId(Long deviceId, Long groceryItemId);
    
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.device.user.id = :userId AND i.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") InventoryStatus status);
    
    Integer countByDeviceId(Long deviceId);
} 