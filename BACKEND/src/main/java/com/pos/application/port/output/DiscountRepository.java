package com.pos.application.port.output;

import com.pos.domain.model.Discount;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port: contract for Discount/Coupon persistence.
 * Implemented by DiscountJpaAdapter in the infrastructure layer.
 */
public interface DiscountRepository {

    Optional<Discount> findByCouponCode(String code);

    Discount save(Discount discount);

    /**
     * Atomically increments the usage count for the given discount.
     * Uses an UPDATE statement to avoid race conditions.
     */
    void incrementUsageCount(UUID discountId);
}
