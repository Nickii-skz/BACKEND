package com.pos.domain.model;

import com.pos.domain.valueobject.PaymentMethod;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the frontend's purchase intent before a Sale is created.
 */
public class Cart {

    private final List<CartItem> items;
    private final PaymentMethod paymentMethod;
    private final String couponCode; // nullable

    public Cart(List<CartItem> items, PaymentMethod paymentMethod, String couponCode) {
        Objects.requireNonNull(items, "Items must not be null");
        Objects.requireNonNull(paymentMethod, "Payment method must not be null");
        this.items = Collections.unmodifiableList(items);
        this.paymentMethod = paymentMethod;
        this.couponCode = couponCode;
    }

    public boolean isEmpty() { return items.isEmpty(); }
    public List<CartItem> getItems() { return items; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getCouponCode() { return couponCode; }
    public boolean hasCoupon() { return couponCode != null && !couponCode.isBlank(); }
}
