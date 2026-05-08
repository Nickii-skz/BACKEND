package com.pos.domain.exception;

public class ProductNotFoundException extends PosException {
    private final String sku;
    public ProductNotFoundException(String sku) {
        super("Product not found with SKU: " + sku);
        this.sku = sku;
    }
    public String getSku() { return sku; }
}
