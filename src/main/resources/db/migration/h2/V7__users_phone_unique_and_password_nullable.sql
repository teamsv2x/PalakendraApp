-- H2: allow NULL password (OTP customers)
ALTER TABLE users ALTER COLUMN password DROP NOT NULL;

-- H2: add unique constraint on phone
ALTER TABLE users ADD CONSTRAINT uk_users_phone UNIQUE (phone);
