-- Insert admin user
-- Password: admin123 (BCrypt encoded: $2a$10$SlYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy.vPf2)
INSERT INTO users (username, email, password, enabled) 
VALUES ('admin', 'admin@hospital.com', '$2a$10$SlYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy.vPf2', true)
ON CONFLICT (username) DO NOTHING;

-- Insert ADMIN role
INSERT INTO roles (name) 
VALUES ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Link admin user to ADMIN role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);
