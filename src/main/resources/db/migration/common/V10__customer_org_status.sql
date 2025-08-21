-- Add status & block metadata to the customer_org link table
ALTER TABLE customer_org
  ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE customer_org
  ADD COLUMN blocked_at TIMESTAMP NULL;

ALTER TABLE customer_org
  ADD COLUMN block_note VARCHAR(255) NULL;

-- Helpful index for filtering
CREATE INDEX idx_customer_org_status ON customer_org(status);
