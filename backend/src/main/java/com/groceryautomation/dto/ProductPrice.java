package com.groceryautomation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for product price information from store APIs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPrice {
    
    private String sku;
    private String productName;
    private Double regularPrice;
    private Double salePrice;
    private boolean onSale;
    private boolean inStock;
    private Integer stockQuantity;
    private String unit;
    private LocalDateTime priceValidUntil;
    
    // Additional metadata from store
    private String imageUrl;
    private String productUrl;
    private String aisle;
    
    /**
     * Get the effective price (sale price if on sale, otherwise regular price)
     */
    public Double getEffectivePrice() {
        return onSale && salePrice != null ? salePrice : regularPrice;
    }
} 