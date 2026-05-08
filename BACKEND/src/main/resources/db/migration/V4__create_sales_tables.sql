CREATE TABLE sales (
    id                UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    subtotal          DECIMAL(10,2)   NOT NULL,
    discount_amount   DECIMAL(10,2)   NOT NULL DEFAULT 0,
    tax_amount        DECIMAL(10,2)   NOT NULL DEFAULT 0,
    total             DECIMAL(10,2)   NOT NULL,
    payment_method    VARCHAR(20)     NOT NULL,
    status            VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    coupon_code       VARCHAR(50),
    created_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    status_updated_at TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(100)
);

CREATE TABLE sale_items (
    id          BIGSERIAL       PRIMARY KEY,
    sale_id     UUID            NOT NULL REFERENCES sales(id),
    sku         VARCHAR(50)     NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    quantity    INT             NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10,2)   NOT NULL,
    subtotal    DECIMAL(10,2)   NOT NULL
);

CREATE INDEX idx_sales_created_at ON sales(created_at);
CREATE INDEX idx_sales_status ON sales(status);
CREATE INDEX idx_sales_payment_method ON sales(payment_method);
CREATE INDEX idx_sale_items_sale_id ON sale_items(sale_id);
