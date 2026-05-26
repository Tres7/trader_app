CREATE TABLE trading_plan_section (
    id UUID PRIMARY KEY,
    trading_plan_id UUID NOT NULL,
    section_key VARCHAR(50) NOT NULL,
    content TEXT,
    comment TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_trading_plan_section_plan
        FOREIGN KEY (trading_plan_id) REFERENCES trading_plan(id) ON DELETE CASCADE,
    CONSTRAINT uq_trading_plan_section_key
        UNIQUE (trading_plan_id, section_key)
);