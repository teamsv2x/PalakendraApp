-- H2 safety: ensure a unique index exists (in case a prior run missed the constraint)
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_phone ON users(phone);
