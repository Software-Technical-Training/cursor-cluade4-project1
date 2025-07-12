package com.groceryautomation.repository;

import com.groceryautomation.entity.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    
    Optional<GroceryItem> findByBarcode(String barcode);
    
    List<GroceryItem> findByCategory(String category);
    
    List<GroceryItem> findByActiveTrue();
    
    List<GroceryItem> findByNameContainingIgnoreCase(String name);
    
    List<GroceryItem> findByCategoryAndActiveTrue(String category);
    
    boolean existsByBarcode(String barcode);
} 