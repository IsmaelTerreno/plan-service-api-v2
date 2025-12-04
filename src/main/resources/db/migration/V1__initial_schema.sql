-- V1: Initial schema for plan-service-api-v2
-- Creates the plan table with all necessary columns and indexes

-- Create plan table
CREATE TABLE IF NOT EXISTS plan (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    invoice_id UUID NOT NULL,
    description VARCHAR(1000) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT false,
    items JSONB NOT NULL,
    status VARCHAR(50) NOT NULL,
    duration_in_days INTEGER NOT NULL,
    expires_at TIMESTAMP  -- Nullable: can be NULL for unlimited plans
);

-- Create indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_plan_user_id ON plan(user_id);
CREATE INDEX IF NOT EXISTS idx_plan_invoice_id ON plan(invoice_id);
CREATE INDEX IF NOT EXISTS idx_plan_status ON plan(status);
CREATE INDEX IF NOT EXISTS idx_plan_is_active ON plan(is_active);
CREATE INDEX IF NOT EXISTS idx_plan_expires_at ON plan(expires_at);
CREATE INDEX IF NOT EXISTS idx_plan_user_active ON plan(user_id, is_active);

-- Add comments to table and columns
COMMENT ON TABLE plan IS 'Stores user subscription plans linked to invoices';
COMMENT ON COLUMN plan.id IS 'Primary key (UUID)';
COMMENT ON COLUMN plan.user_id IS 'User identifier from the authentication service';
COMMENT ON COLUMN plan.invoice_id IS 'Reference to the invoice that created this plan';
COMMENT ON COLUMN plan.description IS 'Human-readable description of the plan';
COMMENT ON COLUMN plan.is_active IS 'Whether the plan is currently active';
COMMENT ON COLUMN plan.items IS 'JSON array of plan items/features';
COMMENT ON COLUMN plan.status IS 'Plan status (e.g., active, expired, cancelled)';
COMMENT ON COLUMN plan.duration_in_days IS 'Plan duration in days';
COMMENT ON COLUMN plan.expires_at IS 'Timestamp when the plan expires (NULL for unlimited)';

