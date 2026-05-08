package com.pos.domain.model;

import com.pos.domain.valueobject.DiscountScope;
import com.pos.domain.valueobject.DiscountType;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.SKU;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a discount/coupon.
 */
public class Discount {

    private final UUID id;
    private final String couponCode;
    private final DiscountType type;
    private final BigDecimal value;
    private final DiscountScope scope;
    private final SKU sku; // only when scope = PRODUCT
    private final int maxUsages; // 0 = unlimited
    private int currentUsages;
    private final Instant expiresAt; // nullable
    private boolean active;
    private final Instant createdAt;

    private Discount(UUID id, String couponCode, DiscountType type, BigDecimal value,
                     DiscountScope scope, SKU sku, int maxUsages, int currentUsages,
                     Instant expiresAt, boolean active, Instant createdAt) {
        this.id = id;
        this.couponCode = couponCode;
        this.type = type;
        this.value = value;
        this.scope = scope;
        this.sku = sku;
        this.maxUsages = maxUsages;
        this.currentUsages = currentUsages;
        this.expiresAt = expiresAt;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Discount create(String couponCode, DiscountType type, BigDecimal value,
                                  DiscountScope scope, SKU sku, int maxUsages, Instant expiresAt) {
        Objects.requireNonNull(couponCode, "Coupon code must not be null");
        Objects.requireNonNull(type, "Discount type must not be null");
        Objects.requireNonNull(value, "Discount value must not be null");
        Objects.requireNonNull(scope, "Discount scope must not be null");
        if (couponCode.isBlank()) throw new IllegalArgumentException("Coupon code must not be blank");
        if (couponCode.length() > 50) throw new IllegalArgumentException("Coupon code must not exceed 50 characters");
        if (value.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Discount value must be greater than zero");
        if (type == DiscountType.PERCENTAGE && value.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new IllegalArgumentException("Percentage discount value must not exceed 100");
        if (scope == DiscountScope.PRODUCT && sku == null)
            throw new IllegalArgumentException("SKU is required for PRODUCT scope discount");
        if (maxUsages < 0) throw new IllegalArgumentException("maxUsages must not be negative");
        return new Discount(UUID.randomUUID(), couponCode, type, value, scope, sku,
                            maxUsages, 0, expiresAt, true, Instant.now());
    }

    public static Discount reconstitute(UUID id, String couponCode, DiscountType type, BigDecimal value,
                                        DiscountScope scope, SKU sku, int maxUsages, int currentUsages,
                                        Instant expiresAt, boolean active, Instant createdAt) {
        return new Discount(id, couponCode, type, value, scope, sku, maxUsages,
                            currentUsages, expiresAt, active, createdAt);
    }

    /**
     * Returns true if this discount is currently valid (active, not expired, not exhausted).
     */
    public boolean isValid() {
        if (!active) return false;
        if (expiresAt != null && Instant.now().isAfter(expiresAt)) return false;
        if (maxUsages > 0 && currentUsages >= maxUsages) return false;
        return true;
    }

    public boolean isExhausted() {
        return maxUsages > 0 && currentUsages >= maxUsages;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    /**
     * Calculates the discount amount to apply to the given subtotal.
     * For PRODUCT scope, pass only the subtotal of matching items.
     */
    public Money calculateDiscount(Money subtotal) {
        Objects.requireNonNull(subtotal, "Subtotal must not be null");
        return switch (type) {
            case PERCENTAGE -> subtotal.multiply(value.divide(BigDecimal.valueOf(100)));
            case FIXED -> {
                // Fixed discount cannot exceed the subtotal
                Money fixedAmount = new Money(value);
                yield fixedAmount.compareTo(subtotal) > 0 ? subtotal : fixedAmount;
            }
        };
    }

    public void incrementUsages() {
        this.currentUsages++;
    }

    // Getters
    public UUID getId() { return id; }
    public String getCouponCode() { return couponCode; }
    public DiscountType getType() { return type; }
    public BigDecimal getValue() { return value; }
    public DiscountScope getScope() { return scope; }
    public SKU getSku() { return sku; }
    public int getMaxUsages() { return maxUsages; }
    public int getCurrentUsages() { return currentUsages; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Discount d)) return false;
        return Objects.equals(id, d.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
