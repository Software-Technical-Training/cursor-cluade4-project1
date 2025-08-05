package com.groceryautomation.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Alternative data seeder for cloud environments
 * This provides programmatic control over data seeding in cloud SQL environments
 * 
 * CURRENTLY DISABLED - No mock data initialization in production PostgreSQL
 * Data seeding only occurs in local H2 development environment via data.sql
 * To enable: uncomment @Component annotation below
 */
// @Component
@RequiredArgsConstructor
@Slf4j
@Profile("cloudsql")
@Order(2) // Run after schema creation
public class CloudDataSeeder implements CommandLineRunner {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public void run(String... args) {
        log.info("Checking if data seeding is needed for cloud environment...");
        
        // Check if data already exists
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        
        if (userCount != null && userCount > 0) {
            log.info("Data already exists in cloud database, skipping seed");
            return;
        }
        
        log.info("Cloud database is empty, but data seeding is handled by SQL scripts");
        log.info("If you need programmatic data seeding, enable this component and implement the logic here");
    }
}