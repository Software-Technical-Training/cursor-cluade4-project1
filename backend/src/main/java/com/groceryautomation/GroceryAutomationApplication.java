package com.groceryautomation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // For mock sensor data generation
public class GroceryAutomationApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GroceryAutomationApplication.class, args);
    }
} 