package com.pos.domain.exception;

public class CouponExhaustedException extends PosException {
    private final String couponCode;
    public CouponExhaustedException(String couponCode) {
        super("Coupon has been exhausted or expired: " + couponCode);
        this.couponCode = couponCode;
    }
    public String getCouponCode() { return couponCode; }
}
