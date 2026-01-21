# WIN777 API Documentation - Phase 2

## Admin Panel APIs

### Admin Authentication

#### POST /admin/login
Admin login endpoint

**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "adminId": 1,
  "username": "admin",
  "email": "admin@win777.com",
  "role": "SUPER_ADMIN",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## User Management APIs

### GET /admin/users
Get all users with pagination

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "mobile": "1234567890",
      "status": "ACTIVE",
      "isBanned": false,
      "deviceCount": 1,
      "createdAt": "2024-01-21T10:00:00",
      "updatedAt": "2024-01-21T10:00:00"
    }
  ],
  "totalPages": 10,
  "totalElements": 100
}
```

### GET /admin/users/search
Search users by mobile number

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Query Parameters:**
- `mobile` (required): Mobile number to search

**Response:**
```json
[
  {
    "id": 1,
    "mobile": "1234567890",
    "status": "ACTIVE",
    "isBanned": false
  }
]
```

### POST /admin/users/{userId}/ban
Ban a user

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Request:**
```json
{
  "reason": "Fraudulent activity detected"
}
```

**Response:**
```
User banned successfully
```

### POST /admin/users/{userId}/unban
Unban a user

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Response:**
```
User unbanned successfully
```

### POST /admin/users/{userId}/adjust-balance
Manually adjust user wallet balance

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Request:**
```json
{
  "amount": 100.00,
  "reason": "Bonus credit"
}
```

**Response:**
```
Balance adjusted successfully
```

---

## Task Management APIs

### POST /admin/tasks
Create or update a task

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Request:**
```json
{
  "title": "Complete SMS Verification",
  "description": "Verify SMS to earn rewards",
  "rewardAmount": 10.00,
  "taskType": "SMS_VERIFICATION",
  "status": "ACTIVE",
  "dailyLimit": 10,
  "minReward": 5.00,
  "maxReward": 15.00,
  "isActive": true
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Complete SMS Verification",
  "rewardAmount": 10.00,
  "taskType": "SMS_VERIFICATION",
  "status": "ACTIVE",
  "dailyLimit": 10
}
```

### DELETE /admin/tasks/{taskId}
Delete a task

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Response:**
```
Task deleted successfully
```

---

## Withdrawal Management APIs

### GET /admin/withdrawals/pending
Get all pending withdrawal requests

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Response:**
```json
[
  {
    "id": 1,
    "userId": 5,
    "amount": 100.00,
    "status": "PENDING",
    "createdAt": "2024-01-21T10:00:00",
    "requestData": {
      "bankAccount": "1234567890",
      "ifscCode": "BANK0001234"
    }
  }
]
```

### POST /admin/withdrawals/{withdrawalId}/approve
Approve a withdrawal request

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "amount": 100.00,
  "status": "APPROVED",
  "approvedBy": 1,
  "approvedAt": "2024-01-21T11:00:00",
  "processedAt": "2024-01-21T11:00:00"
}
```

### POST /admin/withdrawals/{withdrawalId}/reject
Reject a withdrawal request

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Request:**
```json
{
  "reason": "Insufficient documentation"
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 5,
  "amount": 100.00,
  "status": "REJECTED",
  "rejectionReason": "Insufficient documentation",
  "processedAt": "2024-01-21T11:00:00"
}
```

---

## Configuration Management APIs

### GET /config
Get all active configurations (Public endpoint)

**Response:**
```json
[
  {
    "id": 1,
    "configKey": "theme.color",
    "configValue": "#007bff",
    "configType": "STRING",
    "description": "Application theme color",
    "isActive": true
  },
  {
    "id": 2,
    "configKey": "maintenance.mode",
    "configValue": "false",
    "configType": "BOOLEAN",
    "description": "System maintenance mode flag",
    "isActive": true
  }
]
```

### GET /config/{key}
Get specific configuration value (Public endpoint)

**Response:**
```
#007bff
```

### POST /config
Set configuration value (Admin only)

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Request:**
```json
{
  "configKey": "theme.color",
  "configValue": "#ff5733",
  "configType": "STRING",
  "description": "Application theme color"
}
```

**Response:**
```json
{
  "id": 1,
  "configKey": "theme.color",
  "configValue": "#ff5733",
  "configType": "STRING",
  "description": "Application theme color",
  "isActive": true
}
```

### GET /config/maintenance-mode
Check maintenance mode status (Public endpoint)

**Response:**
```json
false
```

### POST /config/maintenance-mode
Set maintenance mode (Admin only)

**Headers:**
```
Authorization: Bearer {admin-token}
Content-Type: application/json
```

**Request:**
```json
true
```

**Response:**
```
Maintenance mode updated
```

### GET /config/theme-color
Get theme color (Public endpoint)

**Response:**
```json
"#007bff"
```

### POST /config/theme-color
Set theme color (Admin only)

**Headers:**
```
Authorization: Bearer {admin-token}
Content-Type: application/json
```

**Request:**
```json
"#ff5733"
```

**Response:**
```
Theme color updated
```

### GET /config/banners
Get active banners (Public endpoint)

**Response:**
```json
[
  {
    "id": 1,
    "title": "Welcome to WIN777",
    "imageUrl": "https://example.com/banner.jpg",
    "linkUrl": "#",
    "displayOrder": 1,
    "isActive": true,
    "startDate": null,
    "endDate": null
  }
]
```

### POST /config/banners
Create or update banner (Admin only)

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Request:**
```json
{
  "title": "New Promotion",
  "imageUrl": "https://example.com/promo.jpg",
  "linkUrl": "/promotions",
  "displayOrder": 2,
  "isActive": true,
  "startDate": "2024-01-21T00:00:00",
  "endDate": "2024-02-21T23:59:59"
}
```

**Response:**
```json
{
  "id": 2,
  "title": "New Promotion",
  "imageUrl": "https://example.com/promo.jpg",
  "linkUrl": "/promotions",
  "displayOrder": 2,
  "isActive": true,
  "startDate": "2024-01-21T00:00:00",
  "endDate": "2024-02-21T23:59:59"
}
```

### DELETE /config/banners/{bannerId}
Delete banner (Admin only)

**Headers:**
```
Authorization: Bearer {admin-token}
```

**Response:**
```
Banner deleted
```

---

## Fraud Prevention Features

### Automatic Fraud Detection
The following fraud prevention mechanisms are automatically applied:

1. **Device Mapping Validation** - Enforced during registration and login
2. **SMS Rate Limiting** - Maximum 10 SMS verifications per hour
3. **Emulator Detection** - Checks device fingerprint for emulator indicators
4. **Multi-SIM Detection** - Validates consistent mobile number usage
5. **Withdrawal Cooldown** - 24-hour cooldown between withdrawals
6. **Ban Enforcement** - Banned users cannot login or perform actions

### Error Responses

#### Device Already Registered
```json
{
  "error": "Device already registered to another account"
}
```

#### SMS Rate Limit Exceeded
```json
{
  "error": "SMS rate limit exceeded. Please try again later."
}
```

#### Account Banned
```json
{
  "error": "Account has been banned: Fraudulent activity detected"
}
```

#### Withdrawal Cooldown Active
```json
{
  "error": "Withdrawal cooldown period active. Please try again later."
}
```

---

## Security Notes

1. All admin endpoints require JWT authentication
2. JWT tokens expire after 24 hours (configurable)
3. Admin actions are logged in the audit trail
4. Fraud detection is automatic and transparent
5. Rate limiting is enforced via Redis
6. All sensitive operations use BCrypt password hashing

## Testing with cURL

### Admin Login
```bash
curl -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Get Users (with token)
```bash
curl -X GET http://localhost:8080/admin/users \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Ban User
```bash
curl -X POST http://localhost:8080/admin/users/1/ban \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"reason":"Fraudulent activity detected"}'
```

### Approve Withdrawal
```bash
curl -X POST http://localhost:8080/admin/withdrawals/1/approve \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```
