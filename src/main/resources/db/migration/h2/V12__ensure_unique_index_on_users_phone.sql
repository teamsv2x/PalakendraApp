-- H2: ensure a unique index also exists (idempotent)
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_phone ON users(phone);
