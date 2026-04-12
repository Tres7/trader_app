CREATE TABLE trading_plan (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);