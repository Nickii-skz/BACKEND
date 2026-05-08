CREATE TABLE discounts (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    coupon_code     VARCHAR(50)     NOT NULL UNIQUE,
    type            VARCHAR(20)     NOT NULL,
    value           DECIMAL(10,2)   NOT NULL CHECK (value > 0),
    scope           VARCHAR(20)     NOT NULL,
    sku             VARCHAR(50)     REFERENCES products(sku),
    max_usages      INT             NOT NULL DEFAULT 0,
    current_usages  INT             NOT NULL DEFAULT 0,
    expires_at      TIMESTAMPTZ,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
