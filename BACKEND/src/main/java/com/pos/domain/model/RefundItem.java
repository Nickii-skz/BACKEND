package com.pos.domain.model;

import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;

import java.util.Objects;

/**
 * Represents a single item in a Refund request.
 */
public class RefundItem {

    private final SKU sku;
    private final String productName;
    private final Quantity quantity;
    private final Money unitPrice;
    private final Money subtotal;

    public RefundItem(SKU sku, Quantity quantity) {
        this(sku, null, quantity, null, null);
    }

    public RefundItem(SKU sku, String productName, Quantity quantity, Money unitPrice, Money subtotal) {
        Objects.requireNonNull(sku, "SKU must not be null");
        Objects.requireNonNull(quantity, "Quantity must not be null");
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public SKU getSku() { return sku; }
    public String getProductName() { return productName; }
    public Quantity getQuantity() { return quantity; }
    public Money getUnitPrice() { return unitPrice; }
    public Money getSubtotal() { return subtotal; }

    @Override
    public String toString() {
        return "RefundItem{sku=" + sku + ", quantity=" + quantity + "}";
    }
}
