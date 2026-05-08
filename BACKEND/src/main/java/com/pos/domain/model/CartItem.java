package com.pos.domain.model;

import java.util.Objects;

/**
 * Represents a single item in a Cart (before the sale is created).
 */
public class CartItem {

    private final String sku;
    private final int quantity;

    public CartItem(String sku, int quantity) {
        Objects.requireNonNull(sku, "SKU must not be null");
        if (sku.isBlank()) throw new IllegalArgumentException("SKU must not be blank");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return "CartItem{sku='" + sku + "', quantity=" + quantity + "}";
    }
}
