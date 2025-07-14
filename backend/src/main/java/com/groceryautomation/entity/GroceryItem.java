package com.groceryautomation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grocery_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroceryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Item name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;
    
    @NotBlank(message = "Unit is required")
    @Column(nullable = false)
    private String unit; // e.g., "gallon", "dozen", "pound", "each"
    
    @Column(unique = true)
    private String barcode;
    
    @NotBlank(message = "SKU is required")
    @Column(nullable = false, unique = true, length = 50)
    private String sku; // Universal SKU for store APIs
    
    private String brand;
    
    private String imageUrl;
    
    // Default threshold for low stock alerts
    @Positive(message = "Default threshold must be positive")
    @Builder.Default
    private Double defaultThreshold = 1.0;
    
    @Builder.Default
    private boolean active = true;
    
    // Nutritional information (optional for POC)
    private Integer caloriesPerUnit;
    private String allergens;
} 