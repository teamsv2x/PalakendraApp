-- MySQL: make password nullable and phone unique
ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NULL;
ALTER TABLE users ADD CONSTRAINT uk_users_phone UNIQUE (phone);
