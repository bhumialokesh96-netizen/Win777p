-- Phase 2 Schema Migration: Fraud Prevention, Admin Panel, Configuration System

-- ========================================
-- FRAUD PREVENTION TABLES
-- ========================================

-- Device mappings table for fraud prevention
CREATE TABLE device_mappings (
    id BIGSERIAL PRIMARY KEY,
    device_fingerprint VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    first_seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_suspicious BOOLEAN DEFAULT FALSE,
    UNIQUE(device_fingerprint)
);

-- Fraud activity logs
CREATE TABLE fraud_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    device_fingerprint VARCHAR(255),
    fraud_type VARCHAR(50) NOT NULL,
    description TEXT,
    severity VARCHAR(20) DEFAULT 'LOW',
    detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB
);

-- User ban tracking
CREATE TABLE user_bans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    banned_by BIGINT REFERENCES users(id),
    reason TEXT,
    ban_type VARCHAR(20) DEFAULT 'MANUAL',
    banned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unbanned_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Withdrawal cooldown tracking
CREATE TABLE withdrawal_cooldowns (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    last_withdrawal_at TIMESTAMP NOT NULL,
    cooldown_until TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- ADMIN PANEL TABLES
-- ========================================

-- Admin users table (separate from regular users)
CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'ADMIN',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Admin audit logs
CREATE TABLE admin_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT NOT NULL REFERENCES admin_users(id),
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(50),
    target_id BIGINT,
    details JSONB,
    ip_address VARCHAR(50),
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- CONFIGURATION SYSTEM TABLES
-- ========================================

-- App configuration table for dynamic settings
CREATE TABLE app_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    config_type VARCHAR(20) DEFAULT 'STRING',
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Banner management
CREATE TABLE banners (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    image_url TEXT,
    link_url TEXT,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- SCHEMA UPDATES
-- ========================================

-- Add additional fields to existing users table for fraud prevention
ALTER TABLE users ADD COLUMN is_banned BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN ban_reason TEXT;
ALTER TABLE users ADD COLUMN is_emulator BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN device_count INTEGER DEFAULT 1;
ALTER TABLE users ADD COLUMN last_withdrawal_at TIMESTAMP;

-- Add approval fields to withdrawals table
ALTER TABLE withdrawals ADD COLUMN approved_by BIGINT REFERENCES admin_users(id);
ALTER TABLE withdrawals ADD COLUMN approved_at TIMESTAMP;
ALTER TABLE withdrawals ADD COLUMN rejection_reason TEXT;

-- Add daily limit tracking to tasks
ALTER TABLE tasks ADD COLUMN daily_limit INTEGER DEFAULT 10;
ALTER TABLE tasks ADD COLUMN min_reward DECIMAL(10, 2);
ALTER TABLE tasks ADD COLUMN max_reward DECIMAL(10, 2);
ALTER TABLE tasks ADD COLUMN is_active BOOLEAN DEFAULT TRUE;

-- ========================================
-- INDEXES FOR PERFORMANCE
-- ========================================

CREATE INDEX idx_device_mappings_device ON device_mappings(device_fingerprint);
CREATE INDEX idx_device_mappings_user ON device_mappings(user_id);
CREATE INDEX idx_fraud_logs_user_id ON fraud_logs(user_id);
CREATE INDEX idx_fraud_logs_detected_at ON fraud_logs(detected_at);
CREATE INDEX idx_user_bans_user_id ON user_bans(user_id);
CREATE INDEX idx_user_bans_active ON user_bans(is_active);
CREATE INDEX idx_withdrawal_cooldowns_user ON withdrawal_cooldowns(user_id);
CREATE INDEX idx_admin_audit_logs_admin ON admin_audit_logs(admin_id);
CREATE INDEX idx_admin_audit_logs_performed_at ON admin_audit_logs(performed_at);
CREATE INDEX idx_app_config_key ON app_config(config_key);
CREATE INDEX idx_banners_active ON banners(is_active);
CREATE INDEX idx_users_banned ON users(is_banned);
