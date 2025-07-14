# Phase 2 Component Test Plan

## Overview
Component tests verify workflows that span multiple classes and layers within the application, without requiring external dependencies like databases or external APIs. These tests use Spring Boot's test slicing features and mock external dependencies while testing real interactions between services, controllers, and repositories.

## Component Test Strategy

### Key Principles
- **Cross-Class Integration**: Test how multiple classes work together
- **Real Spring Context**: Use actual Spring beans and dependency injection
- **Mock External Dependencies**: Mock databases, external APIs, and I/O operations
- **Workflow-Focused**: Test complete user journeys and business processes
- **No Network Calls**: All external services mocked or stubbed

### Test Scope
- Controller → Service → Repository interactions
- Service → Service collaborations
- Complex business workflows
- Error propagation across layers
- Transaction boundaries (with mocked persistence)

## Component Test Categories

### 1. User Registration Workflow (`UserRegistrationComponentTest`)
**Workflow**: User Registration → Device Setup → Store Selection
- **Classes Involved**: `UserController`, `UserService`, `DeviceService`, `StoreService`
- **Test Scenarios**:
  - Complete registration flow with valid data
  - Registration with duplicate email handling
  - Device association during registration
  - Store selection integration
  - Validation error propagation
  - Transaction rollback on failures

### 2. Device Setup Workflow (`DeviceSetupComponentTest`)
**Workflow**: Device Registration → User Association → Inventory Initialization
- **Classes Involved**: `DeviceController`, `DeviceService`, `UserService`, `InventoryService`
- **Test Scenarios**:
  - Device registration with valid user
  - Device status updates across services
  - Inventory initialization for new devices
  - Device deactivation workflow
  - Error handling for invalid devices
  - User-device relationship management

### 3. Store Selection Workflow (`StoreSelectionComponentTest`)
**Workflow**: Store Search → Selection → User Association → Priority Management
- **Classes Involved**: `StoreController`, `StoreService`, `UserService`, `StoreSelectionStrategy`
- **Test Scenarios**:
  - Store search and selection from Google Maps data
  - Primary and backup store configuration
  - Store priority management
  - User-store relationship creation
  - Geographic store filtering
  - Store availability checking

### 4. Inventory Monitoring Workflow (`InventoryMonitoringComponentTest`)
**Workflow**: Sensor Data → Inventory Update → Threshold Checking → Alert Generation
- **Classes Involved**: `MockSensorService`, `InventoryService`, `NotificationService`, `OrderManagementService`
- **Test Scenarios**:
  - Sensor data processing and inventory updates
  - Threshold-based alert generation
  - Low stock detection across multiple items
  - Consumption pattern tracking
  - Inventory sync across devices
  - Alert notification delivery

### 5. Order Management Workflow (`OrderManagementComponentTest`)
**Workflow**: Draft Creation → Price Fetching → User Review → Modification → Submission
- **Classes Involved**: `OrderController`, `OrderManagementService`, `StoreApiService`, `NotificationService`
- **Test Scenarios**:
  - Automatic draft order creation from low inventory
  - Real-time price fetching from store APIs
  - User order modification workflow
  - Order submission to external stores
  - Order status tracking and updates
  - Error handling for failed submissions

### 6. Store API Integration Workflow (`StoreApiIntegrationComponentTest`)
**Workflow**: Price Fetching → Order Submission → Status Tracking → Failover
- **Classes Involved**: `StoreApiService`, `OrderManagementService`, `StoreSelectionStrategy`, `NotificationService`
- **Test Scenarios**:
  - Price fetching from multiple stores
  - Order submission with API integration
  - Store failover when primary unavailable
  - Order status synchronization
  - API error handling and retries
  - Store availability monitoring

## Test Infrastructure

### Base Test Configuration
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.com.groceryautomation=DEBUG"
})
public abstract class ComponentTestBase {
    // Common test infrastructure
}
```

### Mock Configuration
- **Database**: H2 in-memory database with test data
- **External APIs**: `@MockBean` for `StoreApiService`
- **Notifications**: `@MockBean` for email/SMS services
- **Time**: `@MockBean` for `Clock` to control time-based tests

### Test Data Management
- **Test Fixtures**: Reusable test data builders
- **Database State**: Clean state between tests
- **Mock Responses**: Consistent mock data for external services

## Test Execution Strategy

### Phase 1: Core Workflows (Current)
1. User Registration Workflow
2. Device Setup Workflow  
3. Store Selection Workflow

### Phase 2: Business Logic Workflows
4. Inventory Monitoring Workflow
5. Order Management Workflow

### Phase 3: Integration Workflows
6. Store API Integration Workflow

### Success Criteria
- All component tests pass consistently
- Coverage of critical business workflows
- Error scenarios properly handled
- Performance within acceptable limits
- Integration with existing unit tests

## Expected Benefits

### Quality Assurance
- **Workflow Validation**: Ensure complete user journeys work correctly
- **Integration Testing**: Verify service interactions without external dependencies
- **Error Propagation**: Test how errors flow through the system
- **Business Logic**: Validate complex business rules across layers

### Development Efficiency
- **Faster Feedback**: Catch integration issues early
- **Refactoring Safety**: Ensure changes don't break workflows
- **Documentation**: Tests serve as workflow documentation
- **Debugging**: Easier to isolate workflow-specific issues

### Deployment Confidence
- **Pre-Production Validation**: Test realistic scenarios before deployment
- **Regression Prevention**: Catch workflow regressions automatically
- **Performance Baseline**: Monitor workflow performance over time
- **API Contract Testing**: Ensure service contracts are maintained

## Implementation Timeline

### Week 1: Foundation
- Set up component test infrastructure
- Implement User Registration Workflow tests
- Create test data builders and fixtures

### Week 2: Core Workflows
- Implement Device Setup Workflow tests
- Implement Store Selection Workflow tests
- Add error scenario coverage

### Week 3: Business Workflows
- Implement Inventory Monitoring Workflow tests
- Implement Order Management Workflow tests
- Performance and load testing

### Week 4: Advanced Integration
- Implement Store API Integration Workflow tests
- Complete remaining controller tests
- Add repository layer tests

This component test plan bridges the gap between unit tests (isolated class testing) and full integration tests (external dependency testing), providing comprehensive coverage of internal application workflows. 