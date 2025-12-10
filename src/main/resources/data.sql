-- Insert admin user
-- Password: admin123 (BCrypt encoded: $2a$10$SlYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy.vPf2)
INSERT INTO users (username, email, password, full_name, phone, created_at, updated_at) 
VALUES ('admin', 'admin@hospital.com', '$2a$10$SlYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy.vPf2', 'Administrator', '1234567890', NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Insert ADMIN role
INSERT INTO roles (name, created_at, updated_at) 
VALUES ('ROLE_ADMIN', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- Link admin user to ADMIN role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);
