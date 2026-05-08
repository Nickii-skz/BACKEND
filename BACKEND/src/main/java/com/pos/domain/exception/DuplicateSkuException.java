package com.pos.domain.exception;

public class DuplicateSkuException extends PosException {
    private final String sku;
    public DuplicateSkuException(String sku) {
        super("Product with SKU already exists: " + sku);
        this.sku = sku;
    }
    public String getSku() { return sku; }
}
