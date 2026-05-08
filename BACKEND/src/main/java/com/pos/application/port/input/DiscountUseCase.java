package com.pos.application.port.input;

import com.pos.domain.model.Discount;
import com.pos.domain.valueobject.Money;

public interface DiscountUseCase {
    Discount createDiscount(Discount discount);
    Money validateAndApplyCoupon(String couponCode, Money subtotal);
}
