# POC Grocery Automation Project - Setup Conversation

## Date: 2024

## Conversation Summary

### Initial Setup
- User requested to push README changes to GitHub
- Successfully committed and pushed project description

### Custom Instructions Configuration
- Created `.cursorrules` file for project-specific instructions
- Added two key instructions:
  1. Always save prompts and summaries in markdown files
  2. Do not make updates until specifically asked - always plan first and execute after approval

### Project Overview
User described the POC application requirements:

**Main Concept**: An application that receives grocery data from a refrigerator sensor and automatically orders groceries from the nearest store.

**Technology Stack**:
- Frontend: ReactJS (web interface)
- Backend: Java + Spring Boot (service layer)
- Future: React Native for iOS/Android
- Deployment: Google Cloud Platform

**Key Features**:
1. **User Registration & Setup**:
   - Name, email, phone number, address
   - Device ID for sensor
   - Store selection (Google Maps integration showing 5 nearest stores)
   - Payment information (mocked initially)

2. **Main Functionality**:
   - Dashboard showing current groceries (fetched from sensor - mocked)
   - Low grocery alerts
   - Order history
   - Automatic ordering from selected store

**Mocking Strategy**:
- Sensor data will be mocked initially
- Payment processing will be mocked

### Architecture Planning
- Agreed to create high-level architecture documentation
- Chose Mermaid for visual diagrams (version-control friendly, markdown integrated)
- Created documentation structure:
  - `docs/architecture/high-level-architecture.md`
  - `docs/architecture/data-flow-diagrams.md`
  - `docs/prompts/project-setup-conversation.md`

### Storage Recommendations
- Primary Database: PostgreSQL on Cloud SQL
- Cache: Redis on Google Memorystore
- File Storage: Google Cloud Storage (for future use)

## Next Steps
Creating comprehensive architecture documentation with:
- System overview
- Component architecture
- Data flow diagrams
- API design
- Database schema
- Deployment architecture
- Security considerations
- Future mobile app considerations

### GraphQL Addition (Update)
User requested to add GraphQL interface alongside REST endpoints. Implemented:

**GraphQL Features Added:**
- Complete GraphQL schema with types, queries, mutations, and subscriptions
- Query types for user, inventory, stores, and orders
- Mutations for authentication, user management, and order processing
- Real-time subscriptions for inventory updates and alerts
- DataLoader pattern for N+1 query optimization
- GraphQL resolver architecture
- Security considerations (query depth limiting, complexity analysis)
- Performance optimizations (caching, pagination, APQ)
- Decision matrix for when to use GraphQL vs REST

**Technical Implementation:**
- Spring Boot Starter GraphQL integration
- GraphQL endpoint at `/graphql`
- GraphQL Playground at `/graphiql` (development)
- WebSocket support for subscriptions
- Shared service layer between REST and GraphQL

**Documentation Updates:**
1. Added comprehensive GraphQL section to high-level architecture
2. Updated backend architecture to include GraphQL resolvers
3. Added GraphQL resolver flow diagram
4. Added GraphQL subscription flow diagram
5. Updated system architecture diagram to show dual API support
6. Updated technology stack to include REST + GraphQL 