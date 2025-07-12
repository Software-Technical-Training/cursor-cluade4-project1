package com.groceryautomation.service;

import com.groceryautomation.dto.request.UserRegistrationRequest;
import com.groceryautomation.dto.response.UserResponse;
import com.groceryautomation.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    UserResponse registerUser(UserRegistrationRequest request);
    
    Optional<User> findById(Long id);
    
    Optional<User> findByEmail(String email);
    
    UserResponse getUserById(Long id);
    
    List<UserResponse> getAllUsers();
    
    UserResponse updateUser(Long id, UserRegistrationRequest request);
    
    void deleteUser(Long id);
    
    boolean existsByEmail(String email);
} 