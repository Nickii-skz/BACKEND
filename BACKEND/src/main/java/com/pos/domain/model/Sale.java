package com.pos.domain.model;

import com.pos.domain.exception.InvalidOrderTransitionException;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.OrderStatus;
import com.pos.domain.valueobject.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a completed sale transaction.
 *
 * Invariant: total = subtotal - discountAmount + taxAmount
 */
public class Sale {

    private final UUID id;
    private final List<SaleItem> items;
    private final Money subtotal;
    private final Money discountAmount;
    private final Money taxAmount;
    private final Money total;
    private final PaymentMethod paymentMethod;
    private OrderStatus status;
    private final String couponCode;
    private final Instant createdAt;
    private Instant statusUpdatedAt;
    private String createdBy;

    private Sale(UUID id, List<SaleItem> items, Money subtotal, Money discountAmount,
                 Money taxAmount, Money total, PaymentMethod paymentMethod,
                 OrderStatus status, String couponCode, Instant createdAt,
                 Instant statusUpdatedAt, String createdBy) {
        this.id = id;
        this.items = Collections.unmodifiableList(items);
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.couponCode = couponCode;
        this.createdAt = createdAt;
        this.statusUpdatedAt = statusUpdatedAt;
        this.createdBy = createdBy;
    }

    /**
     * Factory method: creates a new Sale from a list of items, applying discount and tax.
     *
     * @param items          non-empty list of SaleItems
     * @param paymentMethod  payment method
     * @param discountAmount discount already calculated (Money.ZERO if none)
     * @param taxRate        tax rate as decimal (e.g. 0.19 for 19%)
     * @param couponCode     optional coupon code applied
     */
    public static Sale create(List<SaleItem> items, PaymentMethod paymentMethod,
                              Money discountAmount, BigDecimal taxRate, String couponCode) {
        Objects.requireNonNull(items, "Items must not be null");
        Objects.requireNonNull(paymentMethod, "Payment method must not be null");
        Objects.requireNonNull(discountAmount, "Discount amount must not be null");
        Objects.requireNonNull(taxRate, "Tax rate must not be null");
        if (items.isEmpty()) throw new IllegalArgumentException("Sale must have at least one item");

        Money subtotal = items.stream()
            .map(SaleItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);

        // Ensure discount doesn't exceed subtotal
        Money effectiveDiscount = discountAmount.compareTo(subtotal) > 0 ? subtotal : discountAmount;

        Money taxBase = subtotal.subtract(effectiveDiscount);
        Money taxAmount = taxBase.multiply(taxRate);
        Money total = taxBase.add(taxAmount);

        // Verify invariant
        Money expectedTotal = subtotal.subtract(effectiveDiscount).add(taxAmount);
        if (total.compareTo(expectedTotal) != 0) {
            throw new IllegalStateException("Sale total invariant violated: total=" + total +
                " expected=" + expectedTotal);
        }

        Instant now = Instant.now();
        return new Sale(UUID.randomUUID(), items, subtotal, effectiveDiscount, taxAmount, total,
                        paymentMethod, OrderStatus.PENDING, couponCode, now, now, null);
    }

    /**
     * Reconstitutes a Sale from persistence.
     */
    public static Sale reconstitute(UUID id, List<SaleItem> items, Money subtotal,
                                    Money discountAmount, Money taxAmount, Money total,
                                    PaymentMethod paymentMethod, OrderStatus status,
                                    String couponCode, Instant createdAt,
                                    Instant statusUpdatedAt, String createdBy) {
        return new Sale(id, items, subtotal, discountAmount, taxAmount, total,
                        paymentMethod, status, couponCode, createdAt, statusUpdatedAt, createdBy);
    }

    /**
     * Transitions the sale to a new OrderStatus.
     * Throws InvalidOrderTransitionException if the transition is not valid.
     */
    public void transitionTo(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus, "New status must not be null");
        if (!this.status.isValidTransitionTo(newStatus)) {
            throw new InvalidOrderTransitionException(this.id, this.status, newStatus);
        }
        this.status = newStatus;
        this.statusUpdatedAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public List<SaleItem> getItems() { return items; }
    public Money getSubtotal() { return subtotal; }
    public Money getDiscountAmount() { return discountAmount; }
    public Money getTaxAmount() { return taxAmount; }
    public Money getTotal() { return total; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public OrderStatus getStatus() { return status; }
    public String getCouponCode() { return couponCode; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getStatusUpdatedAt() { return statusUpdatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public int getItemCount() { return items.size(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale s)) return false;
        return Objects.equals(id, s.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Sale{id=" + id + ", status=" + status + ", total=" + total + "}";
    }
}
