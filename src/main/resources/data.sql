-- Insert admin user
-- Password: admin123 (BCrypt encoded: $2a$10$sTIlqSxLzKqwLbBFgYsK9OPPN7ilwpD3rvDUAGDLDVPg17x1bGxxO)
INSERT INTO users (username, email, password, enabled) 
VALUES ('admin', 'admin@hospital.com', '$2a$10$sTIlqSxLzKqwLbBFgYsK9OPPN7ilwpD3rvDUAGDLDVPg17x1bGxxO', true)
ON CONFLICT (username) DO NOTHING;

-- Insert ADMIN role
INSERT INTO roles (name) 
VALUES ('ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Link admin user to ADMIN role
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);
