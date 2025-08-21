CREATE TABLE otp_codes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  phone VARCHAR(20) NOT NULL,
  code_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  consumed BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- create index in a separate statement (portable for H2 + MySQL)
CREATE INDEX idx_otp_phone ON otp_codes (phone);
