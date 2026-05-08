package com.pos.domain.model;

import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.OrderStatus;
import com.pos.domain.valueobject.PaymentMethod;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable value object representing the receipt generated after a successful sale.
 * Not persisted directly — constructed from a Sale entity.
 */
public record SaleReceipt(
    UUID saleId,
    List<SaleItem> items,
    Money subtotal,
    Money discountAmount,
    Money taxAmount,
    Money total,
    PaymentMethod paymentMethod,
    OrderStatus status,
    String couponCode,
    int itemCount,
    Instant createdAt
) {
    public SaleReceipt {
        Objects.requireNonNull(saleId, "Sale ID must not be null");
        Objects.requireNonNull(items, "Items must not be null");
        Objects.requireNonNull(subtotal, "Subtotal must not be null");
        Objects.requireNonNull(discountAmount, "Discount amount must not be null");
        Objects.requireNonNull(taxAmount, "Tax amount must not be null");
        Objects.requireNonNull(total, "Total must not be null");
        Objects.requireNonNull(paymentMethod, "Payment method must not be null");
        Objects.requireNonNull(status, "Status must not be null");
        Objects.requireNonNull(createdAt, "Created at must not be null");
        if (itemCount <= 0) throw new IllegalArgumentException("Item count must be greater than zero");
    }

    /**
     * Factory method: builds a SaleReceipt from a persisted Sale.
     */
    public static SaleReceipt from(Sale sale) {
        Objects.requireNonNull(sale, "Sale must not be null");
        return new SaleReceipt(
            sale.getId(),
            sale.getItems(),
            sale.getSubtotal(),
            sale.getDiscountAmount(),
            sale.getTaxAmount(),
            sale.getTotal(),
            sale.getPaymentMethod(),
            sale.getStatus(),
            sale.getCouponCode(),
            sale.getItemCount(),
            sale.getCreatedAt()
        );
    }
}
