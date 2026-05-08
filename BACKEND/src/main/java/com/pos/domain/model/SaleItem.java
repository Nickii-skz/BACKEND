package com.pos.domain.model;

import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;

import java.util.Objects;

/**
 * Domain model representing a line item within a Sale.
 * Stores a snapshot of the product price at the time of the sale.
 */
public class SaleItem {

    private final SKU sku;
    private final String productName;
    private final Quantity quantity;
    private final Money unitPrice;   // snapshot at time of sale
    private final Money subtotal;    // unitPrice × quantity

    public SaleItem(SKU sku, String productName, Quantity quantity, Money unitPrice) {
        Objects.requireNonNull(sku, "SKU must not be null");
        Objects.requireNonNull(productName, "Product name must not be null");
        Objects.requireNonNull(quantity, "Quantity must not be null");
        Objects.requireNonNull(unitPrice, "Unit price must not be null");
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(quantity.value());
    }

    public SKU getSku() { return sku; }
    public String getProductName() { return productName; }
    public Quantity getQuantity() { return quantity; }
    public Money getUnitPrice() { return unitPrice; }
    public Money getSubtotal() { return subtotal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaleItem s)) return false;
        return Objects.equals(sku, s.sku);
    }

    @Override
    public int hashCode() { return Objects.hash(sku); }

    @Override
    public String toString() {
        return "SaleItem{sku=" + sku + ", qty=" + quantity + ", unitPrice=" + unitPrice + ", subtotal=" + subtotal + "}";
    }
}
