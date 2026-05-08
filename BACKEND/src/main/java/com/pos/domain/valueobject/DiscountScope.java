package com.pos.domain.valueobject;

/**
 * Enum representing the scope of a Discount.
 * PRODUCT: discount applies only to SaleItems matching a specific SKU.
 * CART: discount applies to the entire sale subtotal.
 */
public enum DiscountScope {
    PRODUCT,
    CART
}
