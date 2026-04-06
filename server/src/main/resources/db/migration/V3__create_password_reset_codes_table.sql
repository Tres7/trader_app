CREATE TABLE password_reset_codes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL,
    used_at TIMESTAMP NULL,
    CONSTRAINT fk_password_reset_codes_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);