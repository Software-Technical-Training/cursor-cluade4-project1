# Grocery Automation Backend - Phase 1

This is the Spring Boot backend for the Grocery Automation POC application.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Running the Application

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or if on Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

The application will start on `http://localhost:8080`

## Testing Interfaces

### 1. Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### 2. H2 Database Console
View and query the in-memory database at:
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

### 3. Actuator Health Check
```
http://localhost:8080/actuator/health
```

## Test Data

The application automatically seeds test data on startup:

- **Test User**: 
  - Email: `john.doe@example.com`
  - Password: `Test@123`
  - User ID: `1`

- **Test Device**: 
  - Device ID: `FRIDGE-001`
  - Name: `Kitchen Smart Fridge`

- **Test Stores**: 3 stores in San Francisco area
- **Inventory Items**: 10 different grocery items with various stock levels

## API Endpoints to Test

### 1. User Registration
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "password": "Test@123",
    "phone": "+1234567891",
    "address": "456 Oak Street, San Francisco, CA 94105"
  }'
```

### 2. Get User by ID
```bash
curl http://localhost:8080/api/users/1
```

### 3. Get Current Inventory
```bash
curl http://localhost:8080/api/inventory/current/1
```

### 4. Get Inventory Alerts (Low Stock Items)
```bash
curl http://localhost:8080/api/inventory/alerts/1
```

### 5. Get All Users
```bash
curl http://localhost:8080/api/users
```

## Mock Sensor Behavior

The application includes a mock sensor service that:
- Runs every 30 seconds to simulate inventory consumption
- Randomly decreases quantities of items
- Automatically updates inventory status (LOW, CRITICAL, OUT_OF_STOCK)
- Occasionally restocks items (every 5 minutes with 20% chance)

Watch the logs or refresh the inventory endpoints to see changes over time!

## Expected Responses

### Successful User Registration Response:
```json
{
  "success": true,
  "data": {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "phone": "+1234567891",
    "address": "456 Oak Street, San Francisco, CA 94105",
    "active": true,
    "deviceCount": 0,
    "activeOrderCount": 0
  },
  "message": "User registered successfully",
  "timestamp": "2025-07-12T10:30:00"
}
```

### Current Inventory Response:
```json
{
  "success": true,
  "data": {
    "deviceId": "FRIDGE-001",
    "deviceName": "Kitchen Smart Fridge",
    "deviceOnline": true,
    "lastSync": "2025-07-12T10:30:00",
    "items": [
      {
        "id": 1,
        "itemId": 1,
        "name": "Whole Milk",
        "category": "Dairy",
        "unit": "gallon",
        "quantity": 0.3,
        "thresholdQuantity": 0.5,
        "status": "LOW"
      },
      {
        "id": 2,
        "itemId": 2,
        "name": "Eggs",
        "category": "Dairy",
        "unit": "dozen",
        "quantity": 0.0,
        "thresholdQuantity": 0.5,
        "status": "OUT_OF_STOCK"
      }
    ],
    "totalItems": 8,
    "lowStockItems": 3,
    "outOfStockItems": 1
  },
  "timestamp": "2025-07-12T10:30:00"
}
```

## Database Schema

Access H2 Console to view:
- USERS table
- DEVICES table
- STORES table
- GROCERY_ITEMS table
- INVENTORY_ITEMS table
- ORDERS table
- ORDER_ITEMS table

## Next Steps

1. **Phase 2**: Add order creation logic
2. **Phase 3**: Build React frontend
3. **Phase 4**: Add authentication (JWT)
4. **Phase 5**: Deploy to Google Cloud Platform 