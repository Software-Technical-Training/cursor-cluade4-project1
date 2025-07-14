package com.groceryautomation.repository;

import com.groceryautomation.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    List<Store> findByActiveTrue();
    
    Optional<Store> findByGooglePlaceId(String googlePlaceId);
    
    Optional<Store> findByNameAndAddress(String name, String address);
    
    List<Store> findByActiveTrueAndAcceptingOrdersTrue();
    
    // Find stores within a radius (in miles) - using Haversine formula
    @Query(value = "SELECT *, " +
            "(3959 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(latitude)) * " +
            "COS(RADIANS(longitude) - RADIANS(:longitude)) + SIN(RADIANS(:latitude)) * " +
            "SIN(RADIANS(latitude)))) AS distance " +
            "FROM stores " +
            "WHERE active = true AND " +
            "(3959 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(latitude)) * " +
            "COS(RADIANS(longitude) - RADIANS(:longitude)) + SIN(RADIANS(:latitude)) * " +
            "SIN(RADIANS(latitude)))) < :radius " +
            "ORDER BY distance " +
            "LIMIT :limit", 
            nativeQuery = true)
    List<Store> findNearbyStores(@Param("latitude") Double latitude, 
                                @Param("longitude") Double longitude,
                                @Param("radius") Double radius,
                                @Param("limit") Integer limit);
    
    List<Store> findByNameContainingIgnoreCase(String name);
} 