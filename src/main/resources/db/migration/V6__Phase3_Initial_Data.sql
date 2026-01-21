-- Phase 3 Initial Data: Default Brand, Cache Configuration, and Sample Analytics

-- ========================================
-- DEFAULT BRAND SETUP
-- ========================================

-- Insert default brand (WIN777)
INSERT INTO brands (id, brand_code, brand_name, domain, is_active) 
VALUES (1, 'WIN777', 'WIN777 Platform', 'win777.com', true);

-- Associate existing data with default brand
UPDATE users SET brand_id = 1 WHERE brand_id IS NULL;
UPDATE tasks SET brand_id = 1 WHERE brand_id IS NULL;
UPDATE admin_users SET brand_id = 1 WHERE brand_id IS NULL;
UPDATE banners SET brand_id = 1 WHERE brand_id IS NULL;

-- Insert default brand configurations
INSERT INTO brand_configs (brand_id, config_key, config_value, config_type, description) VALUES
(1, 'theme.primary_color', '#007bff', 'STRING', 'Primary brand color'),
(1, 'theme.secondary_color', '#6c757d', 'STRING', 'Secondary brand color'),
(1, 'theme.logo_url', '/assets/logo.png', 'STRING', 'Brand logo URL'),
(1, 'theme.app_name', 'WIN777', 'STRING', 'Application name'),
(1, 'features.referral_enabled', 'true', 'BOOLEAN', 'Enable referral system'),
(1, 'features.sms_verification_enabled', 'true', 'BOOLEAN', 'Enable SMS verification'),
(1, 'limits.min_withdrawal', '100', 'NUMBER', 'Minimum withdrawal amount'),
(1, 'limits.max_withdrawal', '10000', 'NUMBER', 'Maximum withdrawal amount'),
(1, 'limits.daily_task_limit', '10', 'NUMBER', 'Daily task completion limit'),
(1, 'contact.support_email', 'support@win777.com', 'STRING', 'Support email address'),
(1, 'contact.support_phone', '+1234567890', 'STRING', 'Support phone number');

-- ========================================
-- CACHE CONFIGURATION
-- ========================================

INSERT INTO cache_config (cache_key, ttl_seconds, cache_type, is_enabled, description) VALUES
('user:profile:', 3600, 'REDIS', true, 'User profile cache'),
('user:balance:', 300, 'REDIS', true, 'User wallet balance cache'),
('tasks:active', 1800, 'REDIS', true, 'Active tasks list cache'),
('config:brand:', 3600, 'REDIS', true, 'Brand configuration cache'),
('fraud:device:', 7200, 'REDIS', true, 'Device fraud check cache'),
('rate_limit:sms:', 3600, 'REDIS', true, 'SMS rate limiting cache'),
('withdrawal:cooldown:', 86400, 'REDIS', true, 'Withdrawal cooldown cache'),
('analytics:snapshot:', 300, 'REDIS', true, 'Analytics snapshot cache');

-- ========================================
-- SAMPLE ANALYTICS DATA (Last 7 days)
-- ========================================

-- Sample user growth analytics
INSERT INTO analytics_user_growth (brand_id, date, new_users, active_users, referred_users, total_users, retention_rate) VALUES
(1, CURRENT_DATE - INTERVAL '7 days', 0, 0, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '6 days', 0, 0, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '5 days', 0, 0, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '4 days', 0, 0, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '3 days', 0, 0, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '2 days', 0, 0, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '1 day', 0, 0, 0, 0, 0.0),
(1, CURRENT_DATE, 0, 0, 0, 0, 0.0);

-- Sample wallet transaction analytics
INSERT INTO analytics_wallet_transactions (brand_id, date, transaction_type, total_transactions, total_amount, success_count, failure_count, success_rate) VALUES
(1, CURRENT_DATE - INTERVAL '7 days', 'REWARD', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '7 days', 'WITHDRAWAL', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '6 days', 'REWARD', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '6 days', 'WITHDRAWAL', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '5 days', 'REWARD', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '5 days', 'WITHDRAWAL', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '4 days', 'REWARD', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '4 days', 'WITHDRAWAL', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '3 days', 'REWARD', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '3 days', 'WITHDRAWAL', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '2 days', 'REWARD', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '2 days', 'WITHDRAWAL', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '1 day', 'REWARD', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE - INTERVAL '1 day', 'WITHDRAWAL', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE, 'REWARD', 0, 0.00, 0, 0, 0.0),
(1, CURRENT_DATE, 'WITHDRAWAL', 0, 0.00, 0, 0, 0.0);

-- Sample SMS metrics
INSERT INTO analytics_sms_metrics (brand_id, date, total_sms_sent, verified_count, failed_count, verification_rate, unique_users) VALUES
(1, CURRENT_DATE - INTERVAL '7 days', 0, 0, 0, 0.0, 0),
(1, CURRENT_DATE - INTERVAL '6 days', 0, 0, 0, 0.0, 0),
(1, CURRENT_DATE - INTERVAL '5 days', 0, 0, 0, 0.0, 0),
(1, CURRENT_DATE - INTERVAL '4 days', 0, 0, 0, 0.0, 0),
(1, CURRENT_DATE - INTERVAL '3 days', 0, 0, 0, 0.0, 0),
(1, CURRENT_DATE - INTERVAL '2 days', 0, 0, 0, 0.0, 0),
(1, CURRENT_DATE - INTERVAL '1 day', 0, 0, 0, 0.0, 0),
(1, CURRENT_DATE, 0, 0, 0, 0.0, 0);

-- Initialize metrics snapshot
INSERT INTO analytics_metrics_snapshot (brand_id, metric_name, metric_value, metric_metadata) VALUES
(1, 'total_users', 0, '{"description": "Total registered users"}'),
(1, 'active_users_today', 0, '{"description": "Active users today"}'),
(1, 'total_transactions', 0, '{"description": "Total wallet transactions"}'),
(1, 'pending_withdrawals', 0, '{"description": "Pending withdrawal requests"}'),
(1, 'total_tasks_completed', 0, '{"description": "Total tasks completed"}'),
(1, 'average_completion_rate', 0, '{"description": "Average task completion rate"}');
