CREATE TABLE outbox_events
(
    id              UUID         PRIMARY KEY,
    aggregate_id    UUID         NOT NULL,
    aggregate_type  VARCHAR(100) NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    payload         TEXT         NOT NULL,
    status          VARCHAR(30)  NOT NULL,
    attempts        INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL,
    published_at    TIMESTAMPTZ,
    last_error      TEXT,

    CONSTRAINT chk_outbox_status
        CHECK (
            status IN (
                'PENDING',
                'PROCESSING',
                'PUBLISHED',
                'FAILED'
            )
        ),

    CONSTRAINT chk_outbox_attempts
        CHECK (attempts >= 0)
);

CREATE INDEX idx_outbox_events_status_created_at
    ON outbox_events (status, created_at);

CREATE INDEX idx_outbox_events_aggregate_id
    ON outbox_events (aggregate_id);