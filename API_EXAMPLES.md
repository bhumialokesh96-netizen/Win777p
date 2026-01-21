# WIN777 Backend API Examples

This file contains example API calls for testing the WIN777 backend.

## Health Check

```bash
curl -X GET http://localhost:8080/health
```

## Authentication APIs

### 1. Register a New User

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "mobile": "9876543210",
    "password": "SecurePass123!",
    "deviceFingerprint": "device-abc-123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mobile": "9876543210",
  "userId": 1
}
```

### 2. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "mobile": "9876543210",
    "password": "SecurePass123!"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "mobile": "9876543210",
  "userId": 1
}
```

### 3. Forgot Password (Admin Only)

```bash
curl -X POST http://localhost:8080/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "mobile": "9876543210",
    "newPassword": "NewSecurePass123!"
  }'
```

## Protected APIs (Require JWT Token)

**Note:** Replace `YOUR_JWT_TOKEN` with the token received from login/register.

### 4. Get Tasks

```bash
curl -X GET http://localhost:8080/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

Response:
```json
[
  {
    "id": 1,
    "title": "Complete Profile",
    "description": "Complete your user profile with all required information",
    "rewardAmount": 50.00,
    "taskType": "PROFILE",
    "status": "ACTIVE",
    "createdAt": "2024-01-20T10:00:00",
    "updatedAt": "2024-01-20T10:00:00"
  }
]
```

### 5. Verify SMS

```bash
curl -X POST http://localhost:8080/sms/verify \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "mobile": "9876543210",
    "messageContent": "Your verification code is 123456",
    "verificationCode": "123456"
  }'
```

Response:
```json
{
  "id": 1,
  "userId": 1,
  "mobile": "9876543210",
  "messageHash": "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
  "verificationCode": "123456",
  "status": "VERIFIED",
  "verifiedAt": "2024-01-20T10:30:00",
  "createdAt": "2024-01-20T10:30:00"
}
```

### 6. Get Wallet Summary

```bash
curl -X GET http://localhost:8080/wallet/summary \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

Response:
```json
{
  "userId": 1,
  "balance": 150.00,
  "transactionCount": 3
}
```

### 7. Get Wallet Ledger

```bash
curl -X GET http://localhost:8080/wallet/ledger \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

Response:
```json
[
  {
    "id": 1,
    "userId": 1,
    "transactionType": "CREDIT",
    "amount": 100.00,
    "referenceType": "TASK_REWARD",
    "referenceId": 1,
    "description": "Reward for completing task",
    "createdAt": "2024-01-20T09:00:00"
  },
  {
    "id": 2,
    "userId": 1,
    "transactionType": "CREDIT",
    "amount": 50.00,
    "referenceType": "BONUS",
    "referenceId": null,
    "description": "Welcome bonus",
    "createdAt": "2024-01-20T08:00:00"
  }
]
```

### 8. Request Withdrawal

```bash
curl -X POST http://localhost:8080/withdraw/request \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00,
    "additionalData": {
      "bankAccount": "1234567890",
      "ifscCode": "SBIN0001234",
      "accountHolderName": "John Doe"
    }
  }'
```

Response:
```json
{
  "id": 1,
  "userId": 1,
  "amount": 50.00,
  "status": "PENDING",
  "requestData": {
    "bankAccount": "1234567890",
    "ifscCode": "SBIN0001234",
    "accountHolderName": "John Doe"
  },
  "processedAt": null,
  "createdAt": "2024-01-20T11:00:00",
  "updatedAt": "2024-01-20T11:00:00"
}
```

### 9. Get Withdrawal History

```bash
curl -X GET http://localhost:8080/withdraw/history \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

Response:
```json
[
  {
    "id": 1,
    "userId": 1,
    "amount": 50.00,
    "status": "PENDING",
    "requestData": {
      "bankAccount": "1234567890",
      "ifscCode": "SBIN0001234",
      "accountHolderName": "John Doe"
    },
    "processedAt": null,
    "createdAt": "2024-01-20T11:00:00",
    "updatedAt": "2024-01-20T11:00:00"
  }
]
```

## Testing Workflow

1. **Start Services**: Start PostgreSQL and Redis using Docker Compose
   ```bash
   docker-compose up -d
   ```

2. **Run Application**: Start the Spring Boot application
   ```bash
   mvn spring-boot:run
   ```

3. **Health Check**: Verify the application is running
   ```bash
   curl http://localhost:8080/health
   ```

4. **Register**: Create a new user account
   ```bash
   curl -X POST http://localhost:8080/auth/register \
     -H "Content-Type: application/json" \
     -d '{"mobile": "9876543210", "password": "Test123!", "deviceFingerprint": "test-device"}'
   ```

5. **Save Token**: Copy the JWT token from the response

6. **Test Protected Endpoints**: Use the token to access protected endpoints
   ```bash
   export JWT_TOKEN="your-token-here"
   curl -X GET http://localhost:8080/tasks \
     -H "Authorization: Bearer $JWT_TOKEN"
   ```

## Error Responses

### 400 Bad Request - Validation Error
```json
{
  "mobile": "Invalid mobile number format",
  "password": "Password is required"
}
```

### 400 Bad Request - Business Logic Error
```json
{
  "error": "Mobile number already registered"
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized"
}
```

## Notes

- All timestamps are in ISO 8601 format
- Amounts are in decimal format with 2 decimal places
- JWT tokens expire after 24 hours (configurable)
- Phone numbers should be 10-15 digits
- All protected endpoints require a valid JWT token in the Authorization header
