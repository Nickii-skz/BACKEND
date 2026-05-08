package com.pos.domain.valueobject;

/**
 * Enum representing the type of a Discount.
 * PERCENTAGE: discount expressed as a percentage (0 < value <= 100).
 * FIXED: discount expressed as a fixed monetary amount.
 */
public enum DiscountType {
    PERCENTAGE,
    FIXED
}
