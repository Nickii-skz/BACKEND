CREATE TABLE users (
    id            UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(100)    NOT NULL UNIQUE,
    password_hash VARCHAR(255)    NOT NULL,
    role          VARCHAR(20)     NOT NULL DEFAULT 'CASHIER',
    active        BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Default admin user (password: admin123 — BCrypt hash)
INSERT INTO users (username, password_hash, role)
VALUES ('admin@pos.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

-- Default cashier user (password: cashier123)
INSERT INTO users (username, password_hash, role)
VALUES ('cashier@pos.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CASHIER');
