# WIN777 Phase 3 API Documentation

## Overview
Phase 3 introduces scaling infrastructure, white-label support (multi-brand), and comprehensive analytics capabilities to the WIN777 platform.

## Table of Contents
1. [Brand Management APIs](#brand-management-apis)
2. [Analytics APIs](#analytics-apis)
3. [Monitoring & Health Check](#monitoring--health-check)
4. [Configuration](#configuration)

---

## Brand Management APIs

### 1. Get Brand by Code
**GET** `/api/brands/{brandCode}`

Get brand information by brand code.

**Example:**
```bash
GET /api/brands/WIN777
```

**Response:**
```json
{
  "id": 1,
  "brandCode": "WIN777",
  "brandName": "WIN777 Platform",
  "domain": "win777.com",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### 2. Get Brand by Domain
**GET** `/api/brands/domain/{domain}`

Get brand information by domain.

**Example:**
```bash
GET /api/brands/domain/win777.com
```

### 3. Get All Active Brands
**GET** `/api/brands`

Get list of all active brands.

**Example:**
```bash
GET /api/brands
```

### 4. Get Brand Configuration
**GET** `/api/brands/{brandCode}/config/{configKey}`

Get specific configuration value for a brand.

**Example:**
```bash
GET /api/brands/WIN777/config/theme.primary_color
```

**Response:**
```json
"#007bff"
```

### 5. Get All Brand Configurations
**GET** `/api/brands/{brandCode}/config`

Get all configuration key-value pairs for a brand.

**Example:**
```bash
GET /api/brands/WIN777/config
```

**Response:**
```json
{
  "theme.primary_color": "#007bff",
  "theme.secondary_color": "#6c757d",
  "theme.logo_url": "/assets/logo.png",
  "features.referral_enabled": "true",
  "limits.min_withdrawal": "100",
  "contact.support_email": "support@win777.com"
}
```

### 6. Create Brand (Admin)
**POST** `/api/brands/admin/create`

Create a new brand (Admin only).

**Parameters:**
- `brandCode` (required): Unique brand code
- `brandName` (required): Brand display name
- `domain` (required): Brand domain

**Example:**
```bash
POST /api/brands/admin/create?brandCode=BRAND2&brandName=Brand Two&domain=brand2.com
```

### 7. Set Brand Configuration (Admin)
**POST** `/api/brands/admin/{brandCode}/config`

Set or update brand configuration (Admin only).

**Parameters:**
- `configKey` (required): Configuration key
- `configValue` (required): Configuration value
- `configType` (optional): Type (STRING, NUMBER, BOOLEAN, etc.)
- `description` (optional): Description of the configuration

**Example:**
```bash
POST /api/brands/admin/WIN777/config
  ?configKey=theme.primary_color
  &configValue=#ff0000
  &configType=STRING
  &description=Primary theme color
```

### 8. Delete Brand Configuration (Admin)
**DELETE** `/api/brands/admin/{brandCode}/config/{configKey}`

Delete brand configuration (Admin only).

**Example:**
```bash
DELETE /api/brands/admin/WIN777/config/theme.test_key
```

---

## Analytics APIs

### 1. Get Task Engagement Analytics
**GET** `/api/analytics/{brandCode}/task-engagement`

Get task engagement analytics for a date range.

**Parameters:**
- `startDate` (required): Start date (ISO format: YYYY-MM-DD)
- `endDate` (required): End date (ISO format: YYYY-MM-DD)

**Example:**
```bash
GET /api/analytics/WIN777/task-engagement?startDate=2024-01-01&endDate=2024-01-31
```

**Response:**
```json
[
  {
    "id": 1,
    "date": "2024-01-15",
    "totalAssignments": 150,
    "completedCount": 120,
    "completionRate": 80.00,
    "totalRewardsPaid": 1200.50,
    "uniqueUsers": 75
  }
]
```

### 2. Get Wallet Transaction Analytics
**GET** `/api/analytics/{brandCode}/wallet-transactions`

Get wallet transaction analytics for a date range.

**Parameters:**
- `startDate` (required): Start date (ISO format: YYYY-MM-DD)
- `endDate` (required): End date (ISO format: YYYY-MM-DD)

**Example:**
```bash
GET /api/analytics/WIN777/wallet-transactions?startDate=2024-01-01&endDate=2024-01-31
```

**Response:**
```json
[
  {
    "id": 1,
    "date": "2024-01-15",
    "transactionType": "REWARD",
    "totalTransactions": 200,
    "totalAmount": 5000.00,
    "successCount": 198,
    "failureCount": 2,
    "successRate": 99.00
  }
]
```

### 3. Get User Growth Analytics
**GET** `/api/analytics/{brandCode}/user-growth`

Get user growth analytics for a date range.

**Parameters:**
- `startDate` (required): Start date (ISO format: YYYY-MM-DD)
- `endDate` (required): End date (ISO format: YYYY-MM-DD)

**Example:**
```bash
GET /api/analytics/WIN777/user-growth?startDate=2024-01-01&endDate=2024-01-31
```

**Response:**
```json
[
  {
    "id": 1,
    "date": "2024-01-15",
    "newUsers": 25,
    "activeUsers": 150,
    "referredUsers": 5,
    "totalUsers": 500,
    "retentionRate": 30.00
  }
]
```

### 4. Get SMS Metrics Analytics
**GET** `/api/analytics/{brandCode}/sms-metrics`

Get SMS metrics analytics for a date range.

**Parameters:**
- `startDate` (required): Start date (ISO format: YYYY-MM-DD)
- `endDate` (required): End date (ISO format: YYYY-MM-DD)

**Example:**
```bash
GET /api/analytics/WIN777/sms-metrics?startDate=2024-01-01&endDate=2024-01-31
```

**Response:**
```json
[
  {
    "id": 1,
    "date": "2024-01-15",
    "totalSmsSent": 300,
    "verifiedCount": 285,
    "failedCount": 15,
    "verificationRate": 95.00,
    "uniqueUsers": 200
  }
]
```

### 5. Get Metrics Snapshot
**GET** `/api/analytics/{brandCode}/snapshot`

Get real-time metrics snapshot for dashboard.

**Example:**
```bash
GET /api/analytics/WIN777/snapshot
```

**Response:**
```json
{
  "total_users": 5000,
  "active_users_today": 250,
  "total_transactions": 15000,
  "pending_withdrawals": 12,
  "tasks_completed_today": 320,
  "average_completion_rate": 82.50
}
```

### 6. Default Brand Analytics Endpoints

For backward compatibility, all analytics endpoints are also available without specifying a brand code:

- **GET** `/api/analytics/task-engagement`
- **GET** `/api/analytics/wallet-transactions`
- **GET** `/api/analytics/user-growth`
- **GET** `/api/analytics/sms-metrics`
- **GET** `/api/analytics/snapshot`

These endpoints use the default brand (WIN777).

### 7. Manually Trigger Analytics Collection (Admin)
**POST** `/api/analytics/admin/collect`

Manually trigger analytics data collection (Admin only).

**Example:**
```bash
POST /api/analytics/admin/collect
```

**Response:**
```json
"Analytics collection triggered successfully"
```

### 8. Update Metrics Snapshot (Admin)
**POST** `/api/analytics/admin/{brandCode}/update-snapshot`

Manually update metrics snapshot for a specific brand (Admin only).

**Example:**
```bash
POST /api/analytics/admin/WIN777/update-snapshot
```

**Response:**
```json
"Metrics snapshot updated successfully"
```

---

## Monitoring & Health Check

### 1. Health Check
**GET** `/actuator/health`

Get application health status.

**Example:**
```bash
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.0.0"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 407270469632,
        "threshold": 10485760
      }
    }
  }
}
```

### 2. Application Info
**GET** `/actuator/info`

Get application information.

**Example:**
```bash
GET /actuator/info
```

### 3. Prometheus Metrics
**GET** `/actuator/prometheus`

Get Prometheus-formatted metrics.

**Example:**
```bash
GET /actuator/prometheus
```

**Response:**
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="PS Eden Space",} 1.234567E8
...
```

### 4. Application Metrics
**GET** `/actuator/metrics`

Get list of available metrics.

**Example:**
```bash
GET /actuator/metrics
```

### 5. Specific Metric
**GET** `/actuator/metrics/{metricName}`

Get specific metric details.

**Example:**
```bash
GET /actuator/metrics/jvm.memory.used
```

---

## Configuration

### Multi-Brand Configuration

Each brand can have its own configuration stored in `brand_configs` table.

**Configuration Keys:**

#### Theme Configuration
- `theme.primary_color` - Primary brand color (e.g., #007bff)
- `theme.secondary_color` - Secondary brand color
- `theme.logo_url` - Brand logo URL
- `theme.app_name` - Application name

#### Feature Flags
- `features.referral_enabled` - Enable/disable referral system
- `features.sms_verification_enabled` - Enable/disable SMS verification

#### Limits
- `limits.min_withdrawal` - Minimum withdrawal amount
- `limits.max_withdrawal` - Maximum withdrawal amount
- `limits.daily_task_limit` - Daily task completion limit

#### Contact Information
- `contact.support_email` - Support email address
- `contact.support_phone` - Support phone number

### Environment Variables

**Database Configuration:**
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/win777db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

**Redis Configuration:**
```bash
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

**Redis Cluster Configuration (Production):**
```bash
SPRING_DATA_REDIS_CLUSTER_NODES=redis1:6379,redis2:6379,redis3:6379
SPRING_DATA_REDIS_CLUSTER_MAX_REDIRECTS=3
```

**Connection Pool Configuration:**
```bash
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
```

**Actuator Configuration:**
```bash
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
```

### Scheduled Jobs

**Daily Analytics Collection:**
- **Schedule:** Every day at 1:00 AM
- **Cron Expression:** `0 0 1 * * *`
- **Description:** Collects and aggregates analytics data for the previous day

---

## Performance Optimization

### Database Indexes

Phase 3 adds comprehensive indexes for optimal query performance:

**User queries:**
- `idx_users_mobile_brand` - Mobile + Brand composite index
- `idx_users_created_brand` - Created date + Brand
- `idx_users_active_brand` - Active status + Brand

**Task queries:**
- `idx_tasks_active_brand` - Active status + Brand
- `idx_tasks_created_brand` - Created date + Brand

**Wallet queries:**
- `idx_wallet_ledger_user_date` - User + Created date
- `idx_wallet_ledger_type_date` - Transaction type + Created date

**Analytics queries:**
- `idx_task_engagement_brand_date` - Brand + Date (DESC)
- `idx_wallet_analytics_brand_date` - Brand + Date (DESC)
- `idx_user_growth_brand_date` - Brand + Date (DESC)
- `idx_sms_metrics_brand_date` - Brand + Date (DESC)

### Caching Strategy

**Redis Cache Configuration:**
- `brands` cache - TTL: 3600s (1 hour)
- `brandConfigs` cache - TTL: 3600s (1 hour)
- `analytics:task` cache - TTL: 3600s (1 hour)
- `analytics:wallet` cache - TTL: 3600s (1 hour)
- `analytics:user` cache - TTL: 3600s (1 hour)
- `analytics:sms` cache - TTL: 3600s (1 hour)
- `analytics:snapshot` cache - TTL: 300s (5 minutes)

### Connection Pooling

**HikariCP Configuration:**
- Maximum pool size: 20
- Minimum idle connections: 5
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes
- Max lifetime: 30 minutes

---

## Monitoring Integration

### Prometheus Scraping

Configure Prometheus to scrape metrics from the application:

```yaml
scrape_configs:
  - job_name: 'win777-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

### Grafana Dashboards

Import pre-built Spring Boot dashboards in Grafana:
- JVM Dashboard
- Spring Boot Statistics
- Custom application metrics

**Access Grafana:**
- URL: http://localhost:3001
- Username: admin
- Password: admin

**Access Prometheus:**
- URL: http://localhost:9090

---

## Security Considerations

1. All admin endpoints require authentication
2. Brand configuration changes are audit-logged
3. Analytics data is isolated by brand
4. Rate limiting applied per brand
5. Database queries use prepared statements
6. Connection pooling prevents resource exhaustion

---

## Error Handling

**Common Error Responses:**

**404 Not Found:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Brand not found",
  "path": "/api/brands/INVALID"
}
```

**400 Bad Request:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid date format",
  "path": "/api/analytics/WIN777/user-growth"
}
```

**500 Internal Server Error:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/analytics/collect"
}
```

---

## Support

For issues and questions, please contact the development team or refer to the main README.md file.
