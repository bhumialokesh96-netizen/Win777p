# WIN777 Backend - Phase 1 MVP

## Overview
This is the backend implementation for WIN777 Platform Phase 1, built with Spring Boot and following clean architecture principles.

## Features

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

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL** - Primary database
- **Redis** - Caching and distributed locking
- **JWT** - Authentication
- **BCrypt** - Password hashing
- **Flyway** - Database migrations
- **Maven** - Build tool

## Database Schema

### Non-negotiable Rules
1. **No direct DB access from app** - All access through JPA repositories
2. **No balance column** - All balances computed from wallet_ledger
3. **Only inserts for financial transactions** - Immutable ledger pattern

### Tables
- `users` - User accounts with mobile authentication
- `tasks` - Available tasks for users
- `task_assignments` - User task assignments
- `sms_logs` - SMS verification logs with hash-based duplicate detection
- `wallet_ledger` - Financial transaction ledger (insert-only)
- `withdrawals` - Withdrawal requests and status tracking

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6+

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
```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

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
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Future Enhancements (Phase 2 & 3)

### Phase 2
- Fraud prevention mechanisms
- Admin panel
- Configuration system
- Enhanced rate limiting

### Phase 3
- Multi-brand support
- Advanced analytics
- Scaling improvements
- Performance optimizations

## License

Proprietary - All rights reserved

## Support

For issues and questions, please contact the development team.