package com.groceryautomation.graphql.resolver.mutation;

import com.groceryautomation.dto.request.UserRegistrationRequest;
import com.groceryautomation.dto.response.UserResponse;
import com.groceryautomation.entity.User;
import com.groceryautomation.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserMutationResolver {
    
    private final UserService userService;
    
    @MutationMapping
    public Map<String, Object> registerUser(@Argument Map<String, Object> input) {
        log.info("Registering new user via GraphQL with email: {}", input.get("email"));
        
        Map<String, Object> response = new HashMap<>();
        try {
            UserRegistrationRequest request = UserRegistrationRequest.builder()
                    .name((String) input.get("name"))
                    .email((String) input.get("email"))
                    .password((String) input.get("password"))
                    .phone((String) input.get("phone"))
                    .address((String) input.get("address"))
                    .latitude((Double) input.get("latitude"))
                    .longitude((Double) input.get("longitude"))
                    .build();
            
            UserResponse userResponse = userService.registerUser(request);
            
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", convertToEntity(userResponse));
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("user", null);
        }
        
        return response;
    }
    
    @MutationMapping
    public Map<String, Object> updateUser(@Argument Long id, @Argument Map<String, Object> input) {
        log.info("Updating user {} via GraphQL", id);
        
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponse userResponse = userService.updateUser(id, input);
            
            response.put("success", true);
            response.put("message", "User updated successfully");
            response.put("user", convertToEntity(userResponse));
        } catch (Exception e) {
            log.error("Error updating user {}: {}", id, e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("user", null);
        }
        
        return response;
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