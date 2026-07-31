ALTER TABLE image_jobs
    ADD COLUMN processing_attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN last_error TEXT,
    ADD COLUMN failed_at TIMESTAMPTZ;

ALTER TABLE image_jobs
    ADD CONSTRAINT chk_image_jobs_processing_attempts
        CHECK (processing_attempts >= 0);