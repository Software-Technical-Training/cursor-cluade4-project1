package com.groceryautomation.repository;

import com.groceryautomation.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    List<Store> findByActiveTrue();
    
    List<Store> findByActiveTrueAndAcceptingOrdersTrue();
    
    // Find stores within a radius (in miles) - using Haversine formula
    @Query(value = "SELECT s.*, " +
            "(3959 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
            "cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(s.latitude)))) AS distance " +
            "FROM stores s " +
            "WHERE s.active = true " +
            "HAVING distance < :radius " +
            "ORDER BY distance " +
            "LIMIT :limit", 
            nativeQuery = true)
    List<Store> findNearbyStores(@Param("latitude") Double latitude, 
                                @Param("longitude") Double longitude,
                                @Param("radius") Double radius,
                                @Param("limit") Integer limit);
    
    List<Store> findByNameContainingIgnoreCase(String name);
} 