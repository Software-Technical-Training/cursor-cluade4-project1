package com.groceryautomation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
    @Column(nullable = false)
    private String phone;
    
    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 500)
    private String address;
    
    // Location coordinates for finding nearby stores
    private Double latitude;
    private Double longitude;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Device> devices = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("priority ASC")
    @Builder.Default
    @JsonIgnore
    private List<UserStore> userStores = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private boolean active = true;
    
    // Helper method to get primary store
    @JsonIgnore
    public Store getPrimaryStore() {
        return userStores.stream()
                .filter(UserStore::isActive)
                .filter(UserStore::isPrimary)
                .map(UserStore::getStore)
                .findFirst()
                .orElse(null);
    }
    
    // Helper method to get backup store (priority = 2)
    @JsonIgnore
    public Store getBackupStore() {
        return userStores.stream()
                .filter(UserStore::isActive)
                .filter(us -> us.getPriority() == 2)
                .map(UserStore::getStore)
                .findFirst()
                .orElse(null);
    }
    
    // Helper method to get all active stores ordered by priority
    @JsonIgnore
    public List<Store> getActiveStores() {
        return userStores.stream()
                .filter(UserStore::isActive)
                .sorted((a, b) -> a.getPriority().compareTo(b.getPriority()))
                .map(UserStore::getStore)
                .collect(java.util.stream.Collectors.toList());
    }
} 