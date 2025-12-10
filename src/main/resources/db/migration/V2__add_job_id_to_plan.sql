-- V2: Add job_id column to plan table
-- Adds support for tracking jobs associated with plans

-- Add job_id column to plan table
ALTER TABLE plan ADD COLUMN IF NOT EXISTS job_id VARCHAR(255);

-- Create index for job_id for efficient lookups
CREATE INDEX IF NOT EXISTS idx_plan_job_id ON plan(job_id);

-- Add comment to new column
COMMENT ON COLUMN plan.job_id IS 'Job identifier associated with the plan (optional)';

