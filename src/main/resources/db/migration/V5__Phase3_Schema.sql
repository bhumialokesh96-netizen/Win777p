-- Phase 3 Schema Migration: Scaling, White-Label Support, and Analytics

-- ========================================
-- WHITE-LABEL / MULTI-BRAND SUPPORT
-- ========================================

-- Brands table for multi-tenant white-label support
CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    brand_code VARCHAR(50) UNIQUE NOT NULL,
    brand_name VARCHAR(255) NOT NULL,
    domain VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Brand-specific configuration
CREATE TABLE brand_configs (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT NOT NULL REFERENCES brands(id) ON DELETE CASCADE,
    config_key VARCHAR(255) NOT NULL,
    config_value TEXT,
    config_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(brand_id, config_key)
);

-- Add brand_id to existing tables for data isolation
ALTER TABLE users ADD COLUMN brand_id BIGINT REFERENCES brands(id);
ALTER TABLE tasks ADD COLUMN brand_id BIGINT REFERENCES brands(id);
ALTER TABLE admin_users ADD COLUMN brand_id BIGINT REFERENCES brands(id);
ALTER TABLE banners ADD COLUMN brand_id BIGINT REFERENCES brands(id);

-- Create indexes for brand-based queries
CREATE INDEX idx_users_brand_id ON users(brand_id);
CREATE INDEX idx_tasks_brand_id ON tasks(brand_id);
CREATE INDEX idx_admin_users_brand_id ON admin_users(brand_id);
CREATE INDEX idx_banners_brand_id ON banners(brand_id);

-- ========================================
-- ANALYTICS TABLES
-- ========================================

-- Task engagement analytics
CREATE TABLE analytics_task_engagement (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT REFERENCES brands(id) ON DELETE CASCADE,
    task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    total_assignments INT DEFAULT 0,
    completed_count INT DEFAULT 0,
    completion_rate DECIMAL(5,2),
    total_rewards_paid DECIMAL(15,2) DEFAULT 0,
    unique_users INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(brand_id, task_id, date)
);

-- Wallet transaction analytics
CREATE TABLE analytics_wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT REFERENCES brands(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    transaction_type VARCHAR(50),
    total_transactions INT DEFAULT 0,
    total_amount DECIMAL(15,2) DEFAULT 0,
    success_count INT DEFAULT 0,
    failure_count INT DEFAULT 0,
    success_rate DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(brand_id, date, transaction_type)
);

-- User growth metrics
CREATE TABLE analytics_user_growth (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT REFERENCES brands(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    new_users INT DEFAULT 0,
    active_users INT DEFAULT 0,
    referred_users INT DEFAULT 0,
    total_users INT DEFAULT 0,
    retention_rate DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(brand_id, date)
);

-- SMS task execution metrics
CREATE TABLE analytics_sms_metrics (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT REFERENCES brands(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    total_sms_sent INT DEFAULT 0,
    verified_count INT DEFAULT 0,
    failed_count INT DEFAULT 0,
    verification_rate DECIMAL(5,2),
    unique_users INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(brand_id, date)
);

-- Real-time metrics snapshot (for dashboard)
CREATE TABLE analytics_metrics_snapshot (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT REFERENCES brands(id) ON DELETE CASCADE,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,2),
    metric_metadata JSONB,
    snapshot_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(brand_id, metric_name)
);

-- Create indexes for analytics queries
CREATE INDEX idx_task_engagement_brand_date ON analytics_task_engagement(brand_id, date DESC);
CREATE INDEX idx_wallet_analytics_brand_date ON analytics_wallet_transactions(brand_id, date DESC);
CREATE INDEX idx_user_growth_brand_date ON analytics_user_growth(brand_id, date DESC);
CREATE INDEX idx_sms_metrics_brand_date ON analytics_sms_metrics(brand_id, date DESC);
CREATE INDEX idx_metrics_snapshot_brand ON analytics_metrics_snapshot(brand_id, metric_name);

-- ========================================
-- PERFORMANCE OPTIMIZATION INDEXES
-- ========================================

-- Add composite indexes for common query patterns
CREATE INDEX idx_users_mobile_brand ON users(mobile, brand_id);
CREATE INDEX idx_users_created_brand ON users(created_at DESC, brand_id);
CREATE INDEX idx_users_active_brand ON users(is_banned, brand_id);

CREATE INDEX idx_tasks_active_brand ON tasks(is_active, brand_id);
CREATE INDEX idx_tasks_created_brand ON tasks(created_at DESC, brand_id);

CREATE INDEX idx_wallet_ledger_user_date ON wallet_ledger(user_id, created_at DESC);
CREATE INDEX idx_wallet_ledger_type_date ON wallet_ledger(transaction_type, created_at DESC);

CREATE INDEX idx_withdrawals_status_date ON withdrawals(status, created_at DESC);
CREATE INDEX idx_withdrawals_user_status ON withdrawals(user_id, status);

CREATE INDEX idx_sms_logs_user_date ON sms_logs(user_id, created_at DESC);
CREATE INDEX idx_sms_logs_verified ON sms_logs(is_verified, created_at DESC);

CREATE INDEX idx_fraud_logs_user_date ON fraud_logs(user_id, detected_at DESC);
CREATE INDEX idx_fraud_logs_severity ON fraud_logs(severity, detected_at DESC);

CREATE INDEX idx_device_mappings_suspicious ON device_mappings(is_suspicious, last_seen_at DESC);

-- ========================================
-- MONITORING & HEALTH CHECK TABLES
-- ========================================

-- System health metrics table
CREATE TABLE system_health_metrics (
    id BIGSERIAL PRIMARY KEY,
    metric_type VARCHAR(50) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,2),
    status VARCHAR(20) DEFAULT 'HEALTHY',
    details JSONB,
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_health_metrics_type_date ON system_health_metrics(metric_type, checked_at DESC);

-- API request logs for monitoring
CREATE TABLE api_request_logs (
    id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT REFERENCES brands(id) ON DELETE CASCADE,
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    status_code INT,
    response_time_ms INT,
    user_id BIGINT,
    ip_address VARCHAR(50),
    user_agent TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Partition api_request_logs by month for better performance
CREATE INDEX idx_api_logs_endpoint_date ON api_request_logs(endpoint, created_at DESC);
CREATE INDEX idx_api_logs_status_date ON api_request_logs(status_code, created_at DESC);
CREATE INDEX idx_api_logs_brand_date ON api_request_logs(brand_id, created_at DESC);

-- ========================================
-- REDIS CACHE CONFIGURATION TABLE
-- ========================================

-- Cache configuration for distributed systems
CREATE TABLE cache_config (
    id BIGSERIAL PRIMARY KEY,
    cache_key VARCHAR(255) UNIQUE NOT NULL,
    ttl_seconds INT DEFAULT 3600,
    cache_type VARCHAR(50) DEFAULT 'REDIS',
    is_enabled BOOLEAN DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
