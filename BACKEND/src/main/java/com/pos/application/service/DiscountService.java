package com.pos.application.service;

import com.pos.application.port.input.DiscountUseCase;
import com.pos.application.port.output.DiscountRepository;
import com.pos.domain.exception.CouponExhaustedException;
import com.pos.domain.exception.CouponNotFoundException;
import com.pos.domain.model.Discount;
import com.pos.domain.valueobject.DiscountScope;
import com.pos.domain.valueobject.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiscountService implements DiscountUseCase {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public Discount createDiscount(Discount discount) {
        discountRepository.findByCouponCode(discount.getCouponCode()).ifPresent(d -> {
            throw new IllegalArgumentException("Coupon code already exists: " + discount.getCouponCode());
        });
        return discountRepository.save(discount);
    }

    @Override
    public Money validateAndApplyCoupon(String couponCode, Money subtotal) {
        Discount discount = discountRepository.findByCouponCode(couponCode)
            .orElseThrow(() -> new CouponNotFoundException(couponCode));

        if (discount.isExpired() || !discount.isActive()) {
            throw new CouponExhaustedException(couponCode);
        }
        if (discount.isExhausted()) {
            throw new CouponExhaustedException(couponCode);
        }

        Money discountAmount;
        if (discount.getScope() == DiscountScope.CART) {
            discountAmount = discount.calculateDiscount(subtotal);
        } else {
            // PRODUCT scope: caller must pass the matching subtotal
            discountAmount = discount.calculateDiscount(subtotal);
        }

        discountRepository.incrementUsageCount(discount.getId());
        return discountAmount;
    }
}
