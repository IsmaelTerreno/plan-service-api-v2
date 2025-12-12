-- V3: Add metadata column to plan table
-- Adds support for storing plan package features (logo, colors, positioning, etc.)

-- Add metadata column to plan table
ALTER TABLE plan ADD COLUMN IF NOT EXISTS metadata JSONB;

-- Create index for metadata for efficient lookups (GIN index for JSONB)
CREATE INDEX IF NOT EXISTS idx_plan_metadata ON plan USING GIN (metadata);

-- Add comment to new column
COMMENT ON COLUMN plan.metadata IS 'JSON metadata storing plan features (showLogo, brandColor, highlightYellow, showOnTop, etc.)';

