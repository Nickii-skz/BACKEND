CREATE TABLE products (
    sku          VARCHAR(50)     PRIMARY KEY,
    name         VARCHAR(255)    NOT NULL,
    description  TEXT,
    image_url    VARCHAR(500),
    unit_price   DECIMAL(10,2)   NOT NULL CHECK (unit_price >= 0),
    stock_qty    INT             NOT NULL DEFAULT 0 CHECK (stock_qty >= 0),
    active       BOOLEAN         NOT NULL DEFAULT TRUE,
    category_id  UUID            REFERENCES categories(id),
    created_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100)
);

CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_active ON products(active);
