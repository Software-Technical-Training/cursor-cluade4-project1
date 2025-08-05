# Debug Database Connection Issue

## The Mystery
- Cloud SQL Studio shows 1 user (John Doe)
- REST API shows 4 users
- Health check shows H2 database
- But data persists across deployments (impossible with H2 in-memory)

## Possible Explanations

### 1. Spring Boot Profile Loading Issue
The `cloudsql` profile might not be loading correctly despite SPRING_PROFILES_ACTIVE being set.

### 2. Classpath Issue
H2 might be taking precedence over PostgreSQL in the classpath.

### 3. Configuration Override
Something might be overriding the database configuration at runtime.

## Debug Steps

### Step 1: Add Logging Configuration
Create `application-cloudsql.properties` with:
```properties
# Add debug logging
logging.level.org.springframework.jdbc=DEBUG
logging.level.com.zaxxer.hikari=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.springframework.boot.autoconfigure.jdbc=DEBUG
```

### Step 2: Force PostgreSQL Only
Add to `application-cloudsql.properties`:
```properties
# Explicitly exclude H2
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration
spring.h2.console.enabled=false

# Force PostgreSQL
spring.jpa.database=POSTGRESQL
spring.datasource.platform=postgresql
```

### Step 3: Add a Debug Endpoint
Create a simple controller to check the actual datasource:

```java
@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private DataSource dataSource;
    
    @GetMapping("/database-info")
    public Map<String, String> getDatabaseInfo() throws SQLException {
        Map<String, String> info = new HashMap<>();
        
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            info.put("url", metaData.getURL());
            info.put("driver", metaData.getDriverName());
            info.put("driverVersion", metaData.getDriverVersion());
            info.put("databaseProduct", metaData.getDatabaseProductName());
            info.put("databaseVersion", metaData.getDatabaseProductVersion());
            info.put("userName", metaData.getUserName());
        }
        
        info.put("dataSourceClass", dataSource.getClass().getName());
        return info;
    }
}
```

### Step 4: Check Both Databases
Since data exists somewhere, let's check both:

1. In PostgreSQL (Cloud SQL Studio):
```sql
-- Check if data was ever written
SELECT * FROM users ORDER BY id;
SELECT COUNT(*) as user_count FROM users;
SELECT MAX(id) as max_id FROM users;
```

2. Via API - force a database query:
```bash
# This should show which database is really being used
curl https://your-service-url/api/debug/database-info
```

## Nuclear Option: Remove H2 Completely

In `pom.xml`, comment out H2:
```xml
<!-- Temporarily disable H2 to force PostgreSQL
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
-->
```

This will force the app to use PostgreSQL or fail completely.