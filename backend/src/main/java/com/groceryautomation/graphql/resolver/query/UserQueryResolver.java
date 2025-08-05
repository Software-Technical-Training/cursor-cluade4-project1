package com.groceryautomation.graphql.resolver.query;

import com.groceryautomation.dto.response.UserResponse;
import com.groceryautomation.entity.User;
import com.groceryautomation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserQueryResolver {
    
    private final UserService userService;
    
    @QueryMapping
    public User me() {
        // For POC, return a mock authenticated user
        // In production, this would get the current user from security context
        log.info("Fetching current user from GraphQL");
        try {
            // For now, return the first user if exists
            List<UserResponse> users = userService.getAllUsers();
            if (!users.isEmpty()) {
                return convertToEntity(users.get(0));
            }
            return null;
        } catch (Exception e) {
            log.error("Error fetching current user: {}", e.getMessage());
            return null;
        }
    }
    
    @QueryMapping
    public User user(@Argument Long id) {
        log.info("Fetching user with ID: {} from GraphQL", id);
        try {
            UserResponse userResponse = userService.getUserById(id);
            return convertToEntity(userResponse);
        } catch (Exception e) {
            log.error("Error fetching user {}: {}", id, e.getMessage());
            return null;
        }
    }
    
    @QueryMapping
    public List<User> users() {
        log.info("Fetching all users from GraphQL");
        try {
            return userService.getAllUsers().stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage());
            return List.of();
        }
    }
    
    private User convertToEntity(UserResponse response) {
        return User.builder()
                .id(response.getId())
                .name(response.getName())
                .email(response.getEmail())
                .phone(response.getPhone())
                .address(response.getAddress())
                .latitude(response.getLatitude())
                .longitude(response.getLongitude())
                .build();
    }
}