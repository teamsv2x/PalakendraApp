CREATE TABLE milk_entries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  customer_org_id BIGINT NOT NULL,
  entry_date DATE NOT NULL,
  shift VARCHAR(20) NOT NULL,
  liters DECIMAL(6,2) NOT NULL,
  CONSTRAINT fk_me_co FOREIGN KEY (customer_org_id) REFERENCES customer_org(id),
  CONSTRAINT uk_entry UNIQUE (customer_org_id, entry_date, shift)
);
