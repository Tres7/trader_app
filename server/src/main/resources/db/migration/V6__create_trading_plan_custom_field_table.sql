CREATE TABLE trading_plan_custom_field (
    id UUID PRIMARY KEY,
    trading_plan_id UUID NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    field_value TEXT,
    display_order INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_trading_plan_custom_field_plan
        FOREIGN KEY (trading_plan_id) REFERENCES trading_plan(id) ON DELETE CASCADE
);