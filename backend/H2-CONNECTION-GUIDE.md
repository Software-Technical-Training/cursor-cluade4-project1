# H2 Database Connection Guide

## Important: H2 is an In-Memory Database

The H2 database in this project is configured as an **in-memory database**, which means:
- ✅ The database only exists while the Spring Boot application is running
- ❌ If the app stops, all data is lost
- ❌ You cannot connect to it when the app is not running

## Step-by-Step Connection Instructions

### 1. First, ensure the application is running:
```bash
cd backend
mvn spring-boot:run
```

Wait until you see:
```
Started GroceryAutomationApplication in X.XXX seconds
```

### 2. Open H2 Console in your browser:
```
http://localhost:8080/h2-console
```

### 3. Enter these EXACT connection details:

| Field | Value |
|-------|-------|
| **Saved Settings** | Generic H2 (Embedded) |
| **Setting Name** | Generic H2 (Embedded) |
| **Driver Class** | org.h2.Driver |
| **JDBC URL** | `jdbc:h2:mem:testdb` |
| **User Name** | `sa` |
| **Password** | *(leave empty)* |

### 4. Click "Connect"

## Common Issues and Solutions

### Issue 1: "Database not found"
**Cause**: Using wrong JDBC URL
**Solution**: Make sure you use exactly `jdbc:h2:mem:testdb` (not `jdbc:h2:~/test` or other variations)

### Issue 2: "Connection refused"
**Cause**: Application not running
**Solution**: Start the application first with `mvn spring-boot:run`

### Issue 3: "404 Not Found" when accessing H2 console
**Cause**: Wrong URL or application not started
**Solution**: Use exactly `http://localhost:8080/h2-console` (with the trailing slash)

### Issue 4: Blank page or no response
**Cause**: Browser security settings
**Solution**: Try a different browser or disable ad blockers

## What You'll See When Connected

Once connected, you should see these tables:
- USERS
- DEVICES
- STORES
- GROCERY_ITEMS
- INVENTORY_ITEMS
- ORDERS
- ORDER_ITEMS

## Sample Queries to Test

```sql
-- See all users
SELECT * FROM USERS;

-- See all devices
SELECT * FROM DEVICES;

-- See user-device relationships
SELECT u.name, u.email, d.device_id, d.name as device_name 
FROM USERS u 
JOIN DEVICES d ON u.id = d.user_id;

-- See inventory items
SELECT * FROM INVENTORY_ITEMS;

-- See low stock items
SELECT gi.name, ii.quantity, ii.status 
FROM INVENTORY_ITEMS ii 
JOIN GROCERY_ITEMS gi ON ii.grocery_item_id = gi.id 
WHERE ii.status != 'NORMAL';
```

## Alternative: Use API Instead

If you can't connect to H2 console, you can verify the data using the REST APIs:

```bash
# Get all users
curl http://localhost:8080/api/users

# Get devices for user 1
curl http://localhost:8080/api/devices/user/1

# Get inventory for user 1
curl http://localhost:8080/api/inventory/current/1
```

## Need More Help?

1. Check application logs for errors
2. Ensure no firewall is blocking port 8080
3. Try accessing Swagger UI first: http://localhost:8080/swagger-ui.html
4. If Swagger works but H2 doesn't, it's likely a JDBC URL issue 