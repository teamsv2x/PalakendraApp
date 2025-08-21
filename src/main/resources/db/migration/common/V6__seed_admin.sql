INSERT INTO users (username, phone, email, password, role, active, created_at, updated_at)
VALUES (
  'admin',
  NULL,
  'admin@palakendra.app',
  '$2a$10$S9p6Y7Dk3JtH.rSkUXbXkOfdYQO2G2jOAGkTQOdsuP8Rx0NwvYe3i', -- BCrypt hash of 'admin@123'
  'ADMIN',
  true,
  NOW(),
  NOW()
);