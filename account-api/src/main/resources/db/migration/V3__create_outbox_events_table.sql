CREATE TABLE outbox_events (
    id UUID NOT NULL,
    event_type VARCHAR NOT NULL,
    payload TEXT NOT NULL,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
