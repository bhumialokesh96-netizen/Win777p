-- Insert sample tasks
INSERT INTO tasks (title, description, reward_amount, task_type, status, created_at, updated_at) VALUES
('Complete Profile', 'Complete your user profile with all required information', 50.00, 'PROFILE', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Daily Login', 'Login to the platform daily to earn rewards', 10.00, 'LOGIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Refer a Friend', 'Refer a friend to join the platform', 100.00, 'REFERRAL', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Share on Social Media', 'Share our platform on your social media', 25.00, 'SOCIAL', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Complete Survey', 'Complete the user feedback survey', 75.00, 'SURVEY', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
