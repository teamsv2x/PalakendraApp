CREATE TABLE customer_org (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_id BIGINT NOT NULL,
  organization_id BIGINT NOT NULL,
  CONSTRAINT fk_co_customer FOREIGN KEY (customer_id) REFERENCES customer_profiles(id),
  CONSTRAINT fk_co_org FOREIGN KEY (organization_id) REFERENCES organizations(id),
  CONSTRAINT uk_customer_org UNIQUE (customer_id, organization_id)
);
