# Cloud Run API Testing Guide

Your Grocery Automation Backend is now live on Cloud Run! 🚀

## Access Your API

Your Cloud Run URL should look something like:
```
https://grocery-automation-backend-[random-string]-uw.a.run.app
```

## Key Endpoints to Test

### 1. Swagger UI
Access the interactive API documentation:
```
https://your-cloud-run-url/swagger-ui/index.html
```

### 2. Health Check
Verify the service is running:
```
https://your-cloud-run-url/actuator/health
```

### 3. API Base Path
All API endpoints are under:
```
https://your-cloud-run-url/api/v1/
```

## Test Sequence

### Step 1: Health Check
First, verify the service is healthy:
- Go to: `https://your-cloud-run-url/actuator/health`
- Expected: `{"status":"UP"}`

### Step 2: Access Swagger UI
- Go to: `https://your-cloud-run-url/swagger-ui/index.html`
- You should see all your API endpoints organized by controller

### Step 3: Test User Registration Flow

1. **Register a User** (POST `/api/v1/users/register`)
   ```json
   {
     "email": "test@example.com",
     "name": "Test User",
     "phoneNumber": "+1234567890"
   }
   ```

2. **Get User Details** (GET `/api/v1/users/{userId}`)
   - Use the userId from registration response

3. **Register a Device** (POST `/api/v1/devices/register`)
   ```json
   {
     "deviceId": "TEST-FRIDGE-001",
     "name": "Test Smart Fridge",
     "userId": "your-user-id"
   }
   ```

4. **Select a Store** (POST `/api/v1/users/{userId}/stores`)
   ```json
   {
     "storeId": 1,
     "deliveryAddress": "123 Test St, Test City, TC 12345"
   }
   ```

5. **Check Inventory** (GET `/api/v1/inventory/device/{deviceId}/current`)
   - View current inventory for the device

### Step 4: Test Other Endpoints

- **List All Stores**: GET `/api/v1/stores`
- **Get Store Details**: GET `/api/v1/stores/{storeId}`
- **View Orders**: GET `/api/v1/orders/user/{userId}`

## Sample Data

The application seeds sample data on startup:
- Sample user: john.doe@example.com
- Sample device: FRIDGE-001
- Sample stores: Whole Foods, Safeway, Trader Joe's, Costco
- Sample inventory items with mock sensor data

## Troubleshooting

### CORS Issues
If testing from a web browser, you might encounter CORS issues. The backend is configured to allow:
- http://localhost:3000
- Cloud Run URLs (https://*.run.app)

### Authentication
Currently, the API allows unauthenticated access for testing. In production, you should enable authentication.

### Database
The deployed version uses H2 in-memory database. Data will be reset when the service restarts. For production, migrate to Cloud SQL.

## Next Steps

After testing:
1. Set up Cloud SQL for persistent data
2. Enable authentication
3. Configure custom domain
4. Set up monitoring and alerts
5. Implement CI/CD with GitHub integration

## Useful Commands

### Using curl to test:
```bash
# Health check
curl https://your-cloud-run-url/actuator/health

# List all stores
curl https://your-cloud-run-url/api/v1/stores

# Register a user
curl -X POST https://your-cloud-run-url/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","name":"Test User","phoneNumber":"+1234567890"}'
```

Replace `your-cloud-run-url` with your actual Cloud Run service URL.