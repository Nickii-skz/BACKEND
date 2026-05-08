CREATE TABLE refunds (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    sale_id         UUID            NOT NULL REFERENCES sales(id),
    total_refunded  DECIMAL(10,2)   NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE refund_items (
    id          BIGSERIAL       PRIMARY KEY,
    refund_id   UUID            NOT NULL REFERENCES refunds(id),
    sku         VARCHAR(50)     NOT NULL,
    quantity    INT             NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_refunds_sale_id ON refunds(sale_id);
