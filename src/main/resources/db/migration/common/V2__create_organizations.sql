CREATE TABLE organizations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  address VARCHAR(255),
  manager_user_id BIGINT NOT NULL UNIQUE,
  CONSTRAINT fk_org_manager FOREIGN KEY (manager_user_id) REFERENCES users(id)
);
