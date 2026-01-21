-- Phase 2 Initial Data: Admin User and Configuration

-- Create initial admin user
-- Password: admin123 (BCrypt hash)
INSERT INTO admin_users (username, password_hash, email, role, is_active, created_at, updated_at)
VALUES ('admin', '$2a$10$xYqB.5mG5fQQnJNqYJ8bO.OyXXz3vZ.ZJMdVJh0QnJF0nYwXHdYzK', 'admin@win777.com', 'SUPER_ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Create default configurations
INSERT INTO app_config (config_key, config_value, config_type, description, is_active, created_at, updated_at)
VALUES 
    ('theme.color', '#007bff', 'STRING', 'Application theme color', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('maintenance.mode', 'false', 'BOOLEAN', 'System maintenance mode flag', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('withdrawal.cooldown.hours', '24', 'INTEGER', 'Withdrawal cooldown period in hours', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('sms.rate.limit.per.hour', '10', 'INTEGER', 'Maximum SMS verifications per hour per user', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (config_key) DO NOTHING;

-- Create sample banner
INSERT INTO banners (title, image_url, link_url, display_order, is_active, created_at, updated_at)
VALUES 
    ('Welcome to WIN777', 'https://via.placeholder.com/800x200?text=Welcome+to+WIN777', '#', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
