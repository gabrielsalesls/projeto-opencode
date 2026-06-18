CREATE TABLE notifications (
    id UUID NOT NULL,
    account_id UUID NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
