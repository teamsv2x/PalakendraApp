CREATE TABLE customer_profiles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  full_name VARCHAR(150) NOT NULL,
  CONSTRAINT fk_cp_user FOREIGN KEY (user_id) REFERENCES users(id)
);
