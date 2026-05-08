package com.pos.infrastructure.adapter.output.mock;

import com.pos.application.port.output.DiscountRepository;
import com.pos.domain.model.Discount;
import com.pos.domain.valueobject.DiscountScope;
import com.pos.domain.valueobject.DiscountType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("mock")
public class InMemoryDiscountRepository implements DiscountRepository {

    private final Map<UUID, Discount> storage = new ConcurrentHashMap<>();
    private final Map<String, UUID> couponIndex = new ConcurrentHashMap<>();

    public InMemoryDiscountRepository() {
        // Datos de ejemplo
        Discount summer20 = Discount.create(
            "SUMMER20",
            DiscountType.PERCENTAGE,
            new BigDecimal("20"),
            DiscountScope.CART,
            null,
            100,
            Instant.now().plus(30, ChronoUnit.DAYS)
        );
        
        Discount welcome10 = Discount.create(
            "WELCOME10",
            DiscountType.FIXED,
            new BigDecimal("10"),
            DiscountScope.CART,
            null,
            0,
            null
        );
        
        storage.put(summer20.getId(), summer20);
        storage.put(welcome10.getId(), welcome10);
        couponIndex.put("SUMMER20", summer20.getId());
        couponIndex.put("WELCOME10", welcome10.getId());
    }

    @Override
    public Discount save(Discount discount) {
        storage.put(discount.getId(), discount);
        couponIndex.put(discount.getCouponCode(), discount.getId());
        return discount;
    }

    @Override
    public Optional<Discount> findByCouponCode(String couponCode) {
        UUID id = couponIndex.get(couponCode);
        return id != null ? Optional.ofNullable(storage.get(id)) : Optional.empty();
    }

    @Override
    public void incrementUsageCount(UUID id) {
        Discount discount = storage.get(id);
        if (discount != null) {
            discount.incrementUsages();
        }
    }
}
