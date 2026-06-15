CREATE TABLE accounts (
    id UUID NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);
