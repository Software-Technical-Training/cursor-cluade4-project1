# Grocery Automation POC - Data Flow Diagrams

## Overview
This document contains visual representations of key data flows in the Grocery Automation system using Mermaid diagrams.

## 1. System Architecture Overview

```mermaid
graph TB
    subgraph "User Devices"
        WEB["Web Browser<br/>(ReactJS)"]
        MOBILE["Mobile App<br/>(React Native - Future)"]
    end
    
    subgraph "Google Cloud Platform"
        subgraph "Frontend"
            CDN["Cloud CDN"]
            HOSTING["Firebase Hosting"]
        end
        
        subgraph "API Gateway"
            LB["Load Balancer"]
            API["Spring Boot API<br/>(Cloud Run)"]
            REST["REST<br/>Endpoints"]
            GQL["GraphQL<br/>Endpoint"]
        end
        
        subgraph "Services"
            AUTH["Auth Service"]
            USER["User Service"]
            SENSOR["Sensor Service<br/>(Mock)"]
            STORE["Store Service"]
            ORDER["Order Service"]
            NOTIF["Notification Service"]
        end
        
        subgraph "Data Layer"
            PSQL[("PostgreSQL<br/>(Cloud SQL)")]
            REDIS[("Redis<br/>(Memorystore)")]
            STORAGE["Cloud Storage"]
        end
    end
    
    subgraph "External"
        IOT["IoT Sensors<br/>(Mocked)"]
        MAPS["Google Maps API"]
        PAYMENT["Payment Gateway<br/>(Mocked)"]
    end
    
    WEB --> CDN
    CDN --> HOSTING
    HOSTING --> LB
    MOBILE -.->|Future| LB
    
    LB --> API
    API --> REST
    API --> GQL
    REST --> AUTH
    REST --> USER
    REST --> SENSOR
    REST --> STORE
    REST --> ORDER
    REST --> NOTIF
    GQL --> AUTH
    GQL --> USER
    GQL --> SENSOR
    GQL --> STORE
    GQL --> ORDER
    GQL --> NOTIF
    
    AUTH --> PSQL
    AUTH --> REDIS
    USER --> PSQL
    SENSOR --> REDIS
    SENSOR --> PSQL
    STORE --> PSQL
    STORE --> MAPS
    ORDER --> PSQL
    ORDER --> PAYMENT
    NOTIF --> REDIS
    
    IOT --> SENSOR
    
    style WEB fill:#4CAF50
    style API fill:#2196F3
    style PSQL fill:#FF9800
    style REDIS fill:#F44336
```

## 2. User Registration and Setup Flow

```mermaid
sequenceDiagram
    participant U as User
    participant W as Web App
    participant API as Spring Boot API
    participant AUTH as Auth Service
    participant DB as PostgreSQL
    participant MAPS as Google Maps
    participant REDIS as Redis Cache
    
    Note over U,REDIS: Registration Phase
    U->>W: Navigate to signup
    W->>U: Show registration form
    U->>W: Submit (email, password, name, phone, address)
    W->>API: POST /api/auth/register
    API->>AUTH: Create user account
    AUTH->>DB: Store user data including address
    AUTH->>REDIS: Create session
    AUTH-->>API: Return JWT token
    API-->>W: Registration success + token
    W->>W: Store token
    W->>U: Redirect to setup wizard
    
    Note over U,REDIS: Setup Phase - Device
    U->>W: Enter device ID
    W->>API: POST /api/users/setup/device
    API->>DB: Link device to user
    API-->>W: Device linked
    W->>U: Show store selection
    
    Note over U,REDIS: Setup Phase - Store Selection
    W->>API: GET /api/stores/nearby (using user's address)
    API->>DB: Fetch user address
    API->>MAPS: Get nearby stores based on address
    MAPS-->>API: Store locations
    API->>DB: Fetch store details
    API-->>W: List of up to 5 stores
    W->>U: Display stores on Google Maps
    U->>W: Select preferred store
    W->>API: POST /api/stores/select
    API->>DB: Save user preference
    API-->>W: Store selected
    
    Note over U,REDIS: Setup Phase - Payment (Mock)
    W->>U: Show payment form
    U->>W: Enter mock payment info
    W->>API: POST /api/users/setup/payment
    API->>DB: Save mock payment token
    API-->>W: Setup complete
    W->>U: Redirect to dashboard
```

## 3. Sensor Data Flow and Alert Generation

```mermaid
graph LR
    subgraph "Sensor Layer"
        S1[Sensor 1]
        S2[Sensor 2]
        SN[Sensor N]
        MOCK[Mock Data Generator]
    end
    
    subgraph "Data Processing"
        INGEST[Data Ingestion<br/>Service]
        PROCESS[Inventory<br/>Processor]
        THRESH[Threshold<br/>Checker]
        ALERT[Alert<br/>Generator]
    end
    
    subgraph "Storage"
        CACHE[(Redis<br/>Real-time Data)]
        DB[(PostgreSQL<br/>Historical Data)]
    end
    
    subgraph "Notification"
        NOTIF[Notification<br/>Service]
        DASH[Dashboard<br/>Updates]
        EMAIL[Email<br/>Service]
    end
    
    S1 -.->|Future| INGEST
    S2 -.->|Future| INGEST
    SN -.->|Future| INGEST
    MOCK -->|Current| INGEST
    
    INGEST --> CACHE
    INGEST --> PROCESS
    PROCESS --> DB
    PROCESS --> THRESH
    THRESH -->|Low Stock| ALERT
    ALERT --> NOTIF
    NOTIF --> DASH
    NOTIF --> EMAIL
    
    CACHE --> DASH
    
    style MOCK fill:#FFE082
    style ALERT fill:#EF5350
    style NOTIF fill:#42A5F5
```

## 4. Automatic Order Processing Flow

```mermaid
stateDiagram-v2
    [*] --> LowStockDetected
    LowStockDetected --> CheckUserPreferences
    CheckUserPreferences --> AutoOrderEnabled: Enabled
    CheckUserPreferences --> SendNotification: Disabled
    
    AutoOrderEnabled --> PrepareOrder
    PrepareOrder --> CalculateQuantities
    CalculateQuantities --> CheckStoreAPI
    CheckStoreAPI --> ItemsAvailable: Available
    CheckStoreAPI --> ItemsUnavailable: Not Available
    
    ItemsAvailable --> CreateOrder
    ItemsUnavailable --> NotifyUser
    
    CreateOrder --> ProcessPayment
    ProcessPayment --> PaymentSuccess: Mock Success
    ProcessPayment --> PaymentFailed: Mock Failure
    
    PaymentSuccess --> UpdateInventory
    UpdateInventory --> SendConfirmation
    SendConfirmation --> UpdateDashboard
    UpdateDashboard --> [*]
    
    PaymentFailed --> NotifyUser
    SendNotification --> UserAction
    UserAction --> ManualOrder: User Orders
    UserAction --> Dismiss: User Dismisses
    ManualOrder --> CreateOrder
    Dismiss --> [*]
    NotifyUser --> [*]
```

## 5. Dashboard Data Flow

```mermaid
graph TB
    subgraph "Frontend Components"
        DASH[Dashboard Component]
        GROCERY[Grocery List]
        ALERTS[Alerts Panel]
        STATS[Quick Stats]
        HISTORY[Order History]
    end
    
    subgraph "State Management"
        STORE[Redux Store]
        USER_SLICE[User Slice]
        GROCERY_SLICE[Grocery Slice]
        ORDER_SLICE[Order Slice]
    end
    
    subgraph "API Calls"
        API1[GET /api/inventory/current]
        API2[GET /api/inventory/alerts]
        API3[GET /api/orders/history]
        API4[GET /api/users/profile]
    end
    
    subgraph "Real-time Updates"
        WS[WebSocket Connection]
        SSE[Server-Sent Events]
    end
    
    DASH --> GROCERY
    DASH --> ALERTS
    DASH --> STATS
    DASH --> HISTORY
    
    GROCERY --> GROCERY_SLICE
    ALERTS --> GROCERY_SLICE
    STATS --> ORDER_SLICE
    HISTORY --> ORDER_SLICE
    
    STORE --> USER_SLICE
    STORE --> GROCERY_SLICE
    STORE --> ORDER_SLICE
    
    GROCERY_SLICE --> API1
    GROCERY_SLICE --> API2
    ORDER_SLICE --> API3
    USER_SLICE --> API4
    
    WS --> GROCERY_SLICE
    SSE --> GROCERY_SLICE
    
    style DASH fill:#4CAF50
    style STORE fill:#2196F3
    style WS fill:#FF9800
```

## 6. Database Entity Relationship Diagram

```mermaid
erDiagram
    USERS ||--o{ DEVICES : owns
    USERS ||--o{ USER_STORES : selects
    USERS ||--o{ ORDERS : places
    
    DEVICES ||--o{ INVENTORY : monitors
    
    STORES ||--o{ USER_STORES : selected_by
    STORES ||--o{ ORDERS : fulfills
    
    GROCERY_ITEMS ||--o{ INVENTORY : tracked_in
    GROCERY_ITEMS ||--o{ ORDER_ITEMS : ordered_as
    
    ORDERS ||--o{ ORDER_ITEMS : contains
    
    USERS {
        uuid id PK
        string email UK
        string password_hash
        string name
        string phone
        text address
        timestamp created_at
        timestamp updated_at
    }
    
    DEVICES {
        uuid id PK
        string device_id UK
        uuid user_id FK
        string name
        string status
        timestamp last_sync
        timestamp created_at
    }
    
    STORES {
        uuid id PK
        string name
        text address
        decimal latitude
        decimal longitude
        string phone
        string api_endpoint
    }
    
    USER_STORES {
        uuid user_id FK
        uuid store_id FK
        boolean is_primary
    }
    
    GROCERY_ITEMS {
        uuid id PK
        string name
        string category
        string unit
        string barcode
    }
    
    INVENTORY {
        uuid id PK
        uuid device_id FK
        uuid item_id FK
        decimal quantity
        decimal threshold_quantity
        timestamp last_updated
    }
    
    ORDERS {
        uuid id PK
        uuid user_id FK
        uuid store_id FK
        string status
        decimal total_amount
        timestamp created_at
        timestamp delivered_at
    }
    
    ORDER_ITEMS {
        uuid id PK
        uuid order_id FK
        uuid item_id FK
        decimal quantity
        decimal price
    }
```

## 7. Authentication Flow

```mermaid
sequenceDiagram
    participant U as User
    participant W as Web App
    participant API as API Gateway
    participant AUTH as Auth Service
    participant DB as Database
    participant REDIS as Redis Cache
    
    Note over U,REDIS: Login Flow
    U->>W: Enter credentials
    W->>API: POST /api/auth/login
    API->>AUTH: Validate credentials
    AUTH->>DB: Check user exists
    DB-->>AUTH: User data
    AUTH->>AUTH: Verify password hash
    AUTH->>REDIS: Create session
    AUTH->>AUTH: Generate JWT token
    AUTH-->>API: Return tokens
    API-->>W: Access + Refresh tokens
    W->>W: Store tokens
    
    Note over U,REDIS: Authenticated Request
    U->>W: Access protected resource
    W->>API: GET /api/resource + JWT
    API->>AUTH: Validate JWT
    AUTH->>REDIS: Check session
    REDIS-->>AUTH: Session valid
    AUTH-->>API: Authorized
    API->>API: Process request
    API-->>W: Return data
    
    Note over U,REDIS: Token Refresh
    W->>API: POST /api/auth/refresh
    API->>AUTH: Validate refresh token
    AUTH->>DB: Check token validity
    AUTH->>AUTH: Generate new access token
    AUTH-->>API: New access token
    API-->>W: Updated token
    
    Note over U,REDIS: Logout
    U->>W: Click logout
    W->>API: POST /api/auth/logout
    API->>AUTH: Invalidate session
    AUTH->>REDIS: Remove session
    AUTH-->>API: Logout success
    API-->>W: Confirmation
    W->>W: Clear tokens
```

## 8. Error Handling Flow

```mermaid
graph TB
    subgraph "Error Sources"
        API_ERR[API Errors]
        SENSOR_ERR[Sensor Errors]
        DB_ERR[Database Errors]
        EXT_ERR[External Service Errors]
    end
    
    subgraph "Error Handler"
        CATCH[Error Catcher]
        LOG[Error Logger]
        CLASSIFY[Error Classifier]
    end
    
    subgraph "Error Types"
        AUTH_401[401 Unauthorized]
        FORBID_403[403 Forbidden]
        NOTFOUND_404[404 Not Found]
        SERVER_500[500 Server Error]
        TIMEOUT_504[504 Timeout]
    end
    
    subgraph "User Feedback"
        TOAST[Toast Notification]
        MODAL[Error Modal]
        RETRY[Retry Option]
        FALLBACK[Fallback UI]
    end
    
    API_ERR --> CATCH
    SENSOR_ERR --> CATCH
    DB_ERR --> CATCH
    EXT_ERR --> CATCH
    
    CATCH --> LOG
    CATCH --> CLASSIFY
    
    CLASSIFY --> AUTH_401
    CLASSIFY --> FORBID_403
    CLASSIFY --> NOTFOUND_404
    CLASSIFY --> SERVER_500
    CLASSIFY --> TIMEOUT_504
    
    AUTH_401 --> MODAL
    FORBID_403 --> TOAST
    NOTFOUND_404 --> FALLBACK
    SERVER_500 --> RETRY
    TIMEOUT_504 --> RETRY
    
    style API_ERR fill:#FF5252
    style CATCH fill:#FFC107
    style LOG fill:#9C27B0
```

## 9. GraphQL Resolver Flow

```mermaid
graph TB
    subgraph "Client Request"
        QUERY["GraphQL Query/Mutation"]
        VARS["Variables"]
    end
    
    subgraph "GraphQL Engine"
        PARSE["Query Parser"]
        VALIDATE["Query Validator"]
        EXEC["Execution Engine"]
    end
    
    subgraph "Resolvers"
        ROOT["Root Resolver"]
        FIELD["Field Resolvers"]
        NESTED["Nested Resolvers"]
    end
    
    subgraph "Data Loading"
        BATCH["DataLoader<br/>(Batching)"]
        CACHE_CHECK["Cache Check"]
        DB_QUERY["Database Query"]
    end
    
    subgraph "Services"
        SVC["Service Layer<br/>(Shared with REST)"]
        AUTH_SVC["Auth Service"]
        USER_SVC["User Service"]
        INV_SVC["Inventory Service"]
    end
    
    subgraph "Response"
        BUILD["Response Builder"]
        ERROR["Error Handler"]
        JSON["JSON Response"]
    end
    
    QUERY --> PARSE
    VARS --> PARSE
    PARSE --> VALIDATE
    VALIDATE --> EXEC
    EXEC --> ROOT
    ROOT --> FIELD
    FIELD --> NESTED
    
    FIELD --> BATCH
    NESTED --> BATCH
    BATCH --> CACHE_CHECK
    CACHE_CHECK -->|Hit| BUILD
    CACHE_CHECK -->|Miss| DB_QUERY
    DB_QUERY --> SVC
    
    SVC --> AUTH_SVC
    SVC --> USER_SVC
    SVC --> INV_SVC
    
    SVC --> BUILD
    BUILD --> JSON
    VALIDATE -->|Invalid| ERROR
    SVC -->|Exception| ERROR
    ERROR --> JSON
    
    style QUERY fill:#4CAF50
    style BATCH fill:#2196F3
    style CACHE_CHECK fill:#FF9800
    style ERROR fill:#F44336
```

## 10. GraphQL Subscription Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant WS as WebSocket
    participant GQL as GraphQL Engine
    participant SUB as Subscription Manager
    participant REDIS as Redis PubSub
    participant SVC as Service Layer
    participant DB as Database
    
    Note over C,DB: Subscription Setup
    C->>WS: Connect WebSocket
    WS->>GQL: Establish connection
    C->>GQL: Subscribe to inventoryUpdated
    GQL->>SUB: Register subscription
    SUB->>REDIS: Subscribe to channel
    SUB-->>C: Subscription confirmed
    
    Note over C,DB: Real-time Update Flow
    SVC->>DB: Update inventory
    DB-->>SVC: Confirm update
    SVC->>REDIS: Publish inventory change
    REDIS->>SUB: Notify subscribers
    SUB->>GQL: Process update
    GQL->>C: Push update via WebSocket
    
    Note over C,DB: Client Disconnect
    C->>WS: Disconnect
    WS->>GQL: Close connection
    GQL->>SUB: Unregister subscription
    SUB->>REDIS: Unsubscribe from channel
```

## Summary

These diagrams illustrate the key data flows and interactions within the Grocery Automation POC system. They provide a visual understanding of:

1. Overall system architecture and component relationships (including REST and GraphQL APIs)
2. User journey from registration through setup
3. How sensor data flows through the system
4. Automatic order processing logic
5. Dashboard data aggregation and display
6. Database relationships
7. Authentication and authorization flows
8. Error handling strategies
9. GraphQL resolver execution flow with DataLoader optimization
10. GraphQL subscription flow for real-time updates

Each diagram focuses on a specific aspect of the system, making it easier to understand individual components while maintaining awareness of the overall system design. The dual API approach (REST + GraphQL) provides flexibility for different client needs and use cases. 