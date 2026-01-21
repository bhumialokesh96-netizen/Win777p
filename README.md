# WIN777 Backend - Complete Platform (Phase 1, 2 & 3)

## Overview
This is the backend implementation for WIN777 Platform, built with Spring Boot and following clean architecture principles. The platform now includes complete white-label support, comprehensive analytics, and production-ready scaling infrastructure.

## 🚀 Phase 3 Features (NEW)

### White-Label / Multi-Brand Support
- **Multi-tenant architecture** - Support multiple brands from single codebase
- **Brand-specific configurations** - Customizable theme, features, and limits per brand
- **Data isolation** - Complete separation of brand data
- **Dynamic configuration** - Runtime-adjustable brand settings

### Analytics & Metrics
- **Task Engagement Analytics** - Track task completion rates, rewards, and user engagement
- **Wallet Transaction Analytics** - Monitor transaction success/failure trends
- **User Growth Metrics** - Track new users, active users, and retention rates
- **SMS Metrics** - Monitor SMS verification rates and failures
- **Real-time Dashboard** - Live metrics snapshot for instant insights
- **Scheduled Collection** - Automated daily analytics aggregation

### Scaling & Performance
- **Connection Pooling** - HikariCP for optimal database connections
- **Redis Caching** - Multi-layer caching with configurable TTL
- **Database Indexing** - Comprehensive indexes for all query patterns
- **Redis Cluster Ready** - Support for distributed rate-limiting and caching
- **Horizontal Scaling** - Stateless architecture for easy scaling

### Monitoring & Observability
- **Prometheus Integration** - Comprehensive metrics collection
- **Grafana Dashboards** - Pre-configured visualization
- **Health Checks** - Database, Redis, and system health monitoring
- **Actuator Endpoints** - Spring Boot Actuator for operational insights
- **Docker Support** - Full containerization with multi-stage builds

## Phase 1 Features (MVP)

### Authentication Service
- **POST /auth/register** - User registration with mobile, password (BCrypt), and device fingerprint
- **POST /auth/login** - User login with JWT token generation
- **POST /auth/forgot-password** - Admin-controlled password reset

### Tasks Service
- **GET /tasks** - Retrieve list of tasks
- **POST /sms/verify** - SMS verification with duplicate hash checking

### Wallet Ledger Service
- **GET /wallet/summary** - Get wallet summary with computed balance
- **GET /wallet/ledger** - Get complete wallet transaction history
- Uses ledger-only approach (no balance column, all transactions computed from ledger)

### Withdrawal Service
- **POST /withdraw/request** - Submit withdrawal request
- **GET /withdraw/history** - Get withdrawal history
- Includes Redis-based locking mechanism
- State management (pending → success)

## Phase 2 Features (NEW)

### 1. Fraud Prevention Mechanisms
- **Device-to-Account Mapping**: Ensures unique device to account association
- **SMS Hash Duplication Check**: Prevents duplicate SMS submissions (Phase 1 + enhanced)
- **Rate Limiting**: Redis-based rate limiting for SMS tasks (10 per hour)
- **Emulator Detection**: Heuristics-based detection of emulator usage
- **Multi-SIM Detection**: Validates consistent mobile number usage
- **Withdrawal Cooldown**: 24-hour cooldown period between withdrawals
- **Manual Ban System**: Admin-controlled user banning with reason tracking

### 2. Admin Panel Backend APIs
- **POST /admin/login** - Admin authentication
- **GET /admin/users** - List users with pagination
- **GET /admin/users/search** - Search users by mobile
- **POST /admin/users/{id}/ban** - Ban user with reason
- **POST /admin/users/{id}/unban** - Unban user
- **POST /admin/users/{id}/adjust-balance** - Manual balance adjustment
- **POST /admin/tasks** - Create/update tasks
- **DELETE /admin/tasks/{id}** - Delete task
- **GET /admin/withdrawals/pending** - Get pending withdrawals
- **POST /admin/withdrawals/{id}/approve** - Approve withdrawal
- **POST /admin/withdrawals/{id}/reject** - Reject withdrawal with reason

### 3. Configuration System
- **GET /config** - Get all active configurations
- **GET /config/{key}** - Get specific configuration value
- **POST /config** - Set configuration value (admin only)
- **GET /config/maintenance-mode** - Check maintenance mode status
- **POST /config/maintenance-mode** - Set maintenance mode (admin only)
- **GET /config/theme-color** - Get theme color
- **POST /config/theme-color** - Set theme color (admin only)
- **GET /config/banners** - Get active banners
- **POST /config/banners** - Create/update banner (admin only)
- **DELETE /config/banners/{id}** - Delete banner (admin only)

### 4. Admin Panel Web Interface
Located in `/admin-panel` directory - React.js based web application:
- **Login Page**: Admin authentication
- **Dashboard**: Overview and statistics
- **User Management**: View, search, ban/unban users
- **Task Management**: View and manage tasks
- **Withdrawal Management**: Approve/reject withdrawals
- **Configuration**: Manage system settings

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL** - Primary database
- **Redis** - Caching, distributed locking, and rate limiting
- **JWT** - Authentication
- **BCrypt** - Password hashing
- **Flyway** - Database migrations
- **Maven** - Build tool
- **React.js 18** - Admin panel frontend
- **Axios** - HTTP client for frontend
- **Spring Boot Actuator** - Monitoring and metrics (Phase 3)
- **Micrometer** - Application metrics facade (Phase 3)
- **Prometheus** - Metrics collection and alerting (Phase 3)
- **Grafana** - Metrics visualization (Phase 3)
- **Docker** - Containerization (Phase 3)

## Database Schema

### Non-negotiable Rules
1. **No direct DB access from app** - All access through JPA repositories
2. **No balance column** - All balances computed from wallet_ledger
3. **Only inserts for financial transactions** - Immutable ledger pattern

### Phase 1 & 2 Tables
- `users` - User accounts with mobile authentication, fraud prevention fields
- `tasks` - Available tasks for users with daily limits and reward ranges
- `task_assignments` - User task assignments
- `sms_logs` - SMS verification logs with hash-based duplicate detection
- `wallet_ledger` - Financial transaction ledger (insert-only)
- `withdrawals` - Withdrawal requests with admin approval tracking
- `device_mappings` - Device fingerprint to user mappings (Phase 2)
- `fraud_logs` - Fraud detection activity logs (Phase 2)
- `user_bans` - User ban tracking with reasons (Phase 2)
- `admin_users` - Admin user accounts (Phase 2)
- `admin_audit_logs` - Admin action audit trail (Phase 2)
- `app_config` - Dynamic application configuration (Phase 2)
- `banners` - Banner management for app (Phase 2)

### Phase 3 Tables (NEW)
- `brands` - Multi-brand/white-label management
- `brand_configs` - Brand-specific configurations
- `analytics_task_engagement` - Task completion and engagement metrics
- `analytics_wallet_transactions` - Transaction success/failure analytics
- `analytics_user_growth` - User acquisition and retention metrics
- `analytics_sms_metrics` - SMS verification performance metrics
- `analytics_metrics_snapshot` - Real-time metrics for dashboards
- `system_health_metrics` - System health monitoring data
- `api_request_logs` - API request tracking for monitoring
- `cache_config` - Cache configuration management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+
- Docker & Docker Compose (for containerized deployment)

## Setup Instructions

### 1. Database Setup
```bash
# Create database
createdb win777db

# Or using psql
psql -c "CREATE DATABASE win777db;"
```

### 2. Redis Setup
```bash
# Start Redis (if not running)
redis-server
```

### 3. Configuration
Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/win777db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 4. Build and Run

#### Option A: Traditional Deployment
```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

#### Option B: Docker Deployment (Recommended for Phase 3)
```bash
# Start all services (PostgreSQL, Redis, App, Prometheus, Grafana)
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down
```

**Services URLs:**
- Application: http://localhost:8080
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001 (admin/admin)
- Health Check: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/prometheus

### 5. Phase 3 Monitoring Setup

#### Access Prometheus
1. Open http://localhost:9090
2. Query metrics: `jvm_memory_used_bytes`, `http_server_requests_seconds_count`
3. Create alerts for critical metrics

#### Access Grafana
1. Open http://localhost:3001
2. Login with admin/admin
3. Add Prometheus data source: http://prometheus:9090
4. Import Spring Boot dashboard (ID: 4701)
5. Create custom dashboards for analytics

#### View Health Checks
```bash
curl http://localhost:8080/actuator/health
```

### Frontend (Admin Panel) Setup

```bash
# Navigate to admin panel directory
cd admin-panel

# Install dependencies
npm install

# Start development server
npm start
```

The admin panel will start on `http://localhost:3000`

**Default Admin Credentials:**
- Username: `admin`
- Password: `admin123`

## API Documentation

### Authentication Endpoints

#### Register
```bash
POST /auth/register
Content-Type: application/json

{
  "mobile": "1234567890",
  "password": "securePassword123",
  "deviceFingerprint": "device-uuid-here"
}
```

#### Login
```bash
POST /auth/login
Content-Type: application/json

{
  "mobile": "1234567890",
  "password": "securePassword123"
}
```

#### Forgot Password
```bash
POST /auth/forgot-password
Content-Type: application/json

{
  "mobile": "1234567890",
  "newPassword": "newSecurePassword123"
}
```

### Protected Endpoints
All endpoints below require JWT authentication via Bearer token:
```
Authorization: Bearer <your-jwt-token>
```

#### Get Tasks
```bash
GET /tasks
```

#### Verify SMS
```bash
POST /sms/verify
Content-Type: application/json

{
  "userId": 1,
  "mobile": "1234567890",
  "messageContent": "Your verification message",
  "verificationCode": "123456"
}
```

#### Get Wallet Summary
```bash
GET /wallet/summary
```

#### Get Wallet Ledger
```bash
GET /wallet/ledger
```

#### Request Withdrawal
```bash
POST /withdraw/request
Content-Type: application/json

{
  "amount": 100.00,
  "additionalData": {
    "bankAccount": "1234567890",
    "ifscCode": "BANK0001234"
  }
}
```

#### Get Withdrawal History
```bash
GET /withdraw/history
```

## Architecture

### Clean Architecture Layers
1. **Entity Layer** - Domain models (JPA entities)
2. **Repository Layer** - Data access (Spring Data JPA)
3. **Service Layer** - Business logic
4. **Controller Layer** - REST API endpoints
5. **Security Layer** - JWT authentication & authorization

### Key Design Patterns
- Repository Pattern
- Service Layer Pattern
- DTO Pattern for API requests/responses
- Immutable Ledger Pattern for financial transactions

## Security Features

1. **BCrypt Password Hashing** - Industry-standard password encryption
2. **JWT Authentication** - Stateless authentication with signed tokens
3. **Device Fingerprinting** - Track user devices
4. **Distributed Locking** - Redis-based locks for withdrawal operations
5. **Input Validation** - Bean validation on all request DTOs

## Testing

```bash
# Run backend tests
mvn test

# Run with coverage
mvn test jacoco:report

# Build without tests
mvn clean package -DskipTests

# Run frontend tests (in admin-panel directory)
cd admin-panel
npm test
```

## Deployment Notes

### Production Checklist
1. Update admin password in database migration (V4)
2. Set strong JWT secret in application.properties
3. Configure proper CORS settings
4. Set up SSL/TLS certificates
5. Configure Redis with authentication
6. Review and adjust fraud detection thresholds
7. Set up monitoring and alerting
8. Build admin panel for production: `cd admin-panel && npm run build`

## Future Enhancements (Phase 3) ✅ **COMPLETED**

Phase 3 has been successfully implemented with the following features:

### White-Label / Multi-Brand Support
- ✅ Brand management with unique codes and domains
- ✅ Brand-specific configurations (theme, features, limits)
- ✅ Data isolation per brand
- ✅ RESTful APIs for brand management

### Analytics & Reporting
- ✅ Task engagement analytics
- ✅ Wallet transaction analytics
- ✅ User growth metrics
- ✅ SMS verification metrics
- ✅ Real-time metrics snapshot for dashboards
- ✅ Scheduled daily analytics collection

### Scaling & Performance
- ✅ HikariCP connection pooling
- ✅ Redis caching with configurable TTL
- ✅ Comprehensive database indexing
- ✅ Redis Cluster support (docker-compose)
- ✅ Horizontal scaling ready architecture

### Monitoring & Observability
- ✅ Spring Boot Actuator integration
- ✅ Prometheus metrics endpoint
- ✅ Grafana visualization support
- ✅ Health check endpoints
- ✅ System health metrics tracking
- ✅ Docker containerization

### API Documentation
- ✅ Comprehensive Phase 3 API documentation
- ✅ Brand management endpoints
- ✅ Analytics endpoints
- ✅ Monitoring endpoints

See [PHASE3_API_DOCS.md](./PHASE3_API_DOCS.md) for detailed API documentation.

## Future Enhancements (Phase 4)

### Planned Features
- Fraud detection ML models
- Enhanced reporting system with PDF exports
- Advanced data visualization
- Multi-language support
- Mobile app deeplink integration
- Advanced user segmentation
- A/B testing framework

## License

Proprietary - All rights reserved

## Support

For issues and questions, please contact the development team.