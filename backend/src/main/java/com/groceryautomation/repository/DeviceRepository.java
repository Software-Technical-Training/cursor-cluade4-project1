package com.groceryautomation.repository;

import com.groceryautomation.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    Optional<Device> findByDeviceId(String deviceId);
    
    List<Device> findByUserId(Long userId);
    
    List<Device> findByUserIdAndActive(Long userId, boolean active);
    
    boolean existsByDeviceId(String deviceId);
    
    @Query("SELECT d FROM Device d LEFT JOIN FETCH d.inventoryItems WHERE d.id = :id")
    Optional<Device> findByIdWithInventory(@Param("id") Long id);
    
    @Query("SELECT d FROM Device d WHERE d.active = true AND d.online = true")
    List<Device> findAllActiveAndOnlineDevices();
} 