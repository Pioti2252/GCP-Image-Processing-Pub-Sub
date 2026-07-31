CREATE TABLE image_jobs
(
    id                UUID         PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename   VARCHAR(255) NOT NULL UNIQUE,
    content_type      VARCHAR(100) NOT NULL,
    size_bytes        BIGINT       NOT NULL,
    status            VARCHAR(30)  NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL,
    updated_at        TIMESTAMPTZ  NOT NULL,

    CONSTRAINT chk_image_jobs_size_positive
        CHECK (size_bytes > 0),

    CONSTRAINT chk_image_jobs_status
        CHECK (
            status IN (
                'PENDING',
                'PROCESSING',
                'COMPLETED',
                'FAILED'
            )
        )
);

CREATE INDEX idx_image_jobs_status
    ON image_jobs (status);

CREATE INDEX idx_image_jobs_created_at
    ON image_jobs (created_at);