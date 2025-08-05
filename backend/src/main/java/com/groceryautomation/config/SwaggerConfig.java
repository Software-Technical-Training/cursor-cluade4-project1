package com.groceryautomation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI groceryAutomationOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");
        
        Server cloudRunServer = new Server();
        cloudRunServer.setUrl("https://grocery-automation-backend-952204582614.us-west2.run.app");
        cloudRunServer.setDescription("Cloud Run Server");
        
        Server productionServer = new Server();
        productionServer.setUrl("https://api.groceryautomation.com");
        productionServer.setDescription("Production Server (Future)");
        
        Contact contact = new Contact();
        contact.setEmail("support@groceryautomation.com");
        contact.setName("Grocery Automation Support");
        
        License license = new License()
                .name("Apache 2.0")
                .url("http://www.apache.org/licenses/LICENSE-2.0.html");
        
        Info info = new Info()
                .title("Grocery Automation POC API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides endpoints for the Grocery Automation POC application. " +
                           "It includes user management, inventory tracking, store selection, and automated ordering.")
                .license(license);
        
        return new OpenAPI()
                .info(info)
                .servers(List.of(cloudRunServer, localServer, productionServer));
    }
} 