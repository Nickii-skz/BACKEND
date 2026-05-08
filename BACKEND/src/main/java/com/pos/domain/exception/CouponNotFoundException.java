package com.pos.domain.exception;

public class CouponNotFoundException extends PosException {
    private final String couponCode;
    public CouponNotFoundException(String couponCode) {
        super("Coupon not found: " + couponCode);
        this.couponCode = couponCode;
    }
    public String getCouponCode() { return couponCode; }
}
