package com.pos.infrastructure.adapter.output.persistence;

import com.pos.application.port.output.DiscountRepository;
import com.pos.domain.model.Discount;
import com.pos.domain.valueobject.DiscountScope;
import com.pos.domain.valueobject.DiscountType;
import com.pos.domain.valueobject.SKU;
import com.pos.infrastructure.adapter.output.entity.DiscountEntity;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.util.Optional;
import java.util.UUID;

@Component
@Profile("!mock")
public class DiscountJpaAdapter implements DiscountRepository {

    private final DiscountJpaRepository jpaRepository;

    public DiscountJpaAdapter(DiscountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Discount> findByCouponCode(String code) {
        return jpaRepository.findByCouponCode(code).map(this::toDomain);
    }

    @Override
    public Discount save(Discount discount) {
        return toDomain(jpaRepository.save(toEntity(discount)));
    }

    @Override
    public void incrementUsageCount(UUID discountId) {
        jpaRepository.incrementUsageCount(discountId);
    }

    private Discount toDomain(DiscountEntity e) {
        SKU sku = e.getSku() != null ? new SKU(e.getSku()) : null;
        return Discount.reconstitute(
            e.getId(), e.getCouponCode(),
            DiscountType.valueOf(e.getType()),
            e.getValue(),
            DiscountScope.valueOf(e.getScope()),
            sku, e.getMaxUsages(), e.getCurrentUsages(),
            e.getExpiresAt(), e.isActive(), e.getCreatedAt()
        );
    }

    private DiscountEntity toEntity(Discount d) {
        DiscountEntity e = new DiscountEntity();
        e.setId(d.getId());
        e.setCouponCode(d.getCouponCode());
        e.setType(d.getType().name());
        e.setValue(d.getValue());
        e.setScope(d.getScope().name());
        e.setSku(d.getSku() != null ? d.getSku().value() : null);
        e.setMaxUsages(d.getMaxUsages());
        e.setCurrentUsages(d.getCurrentUsages());
        e.setExpiresAt(d.getExpiresAt());
        e.setActive(d.isActive());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
}
