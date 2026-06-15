CREATE TABLE transfers (
    id UUID NOT NULL,
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transfers_source_account
        FOREIGN KEY (source_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transfers_destination_account
        FOREIGN KEY (destination_account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_transfers_source_account_id ON transfers (source_account_id);
CREATE INDEX idx_transfers_destination_account_id ON transfers (destination_account_id);
