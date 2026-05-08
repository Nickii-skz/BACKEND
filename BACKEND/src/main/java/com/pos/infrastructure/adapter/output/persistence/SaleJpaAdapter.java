package com.pos.infrastructure.adapter.output.persistence;

import com.pos.application.port.output.SaleRepository;
import com.pos.domain.exception.SaleNotFoundException;
import com.pos.domain.model.Sale;
import com.pos.domain.model.SaleItem;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.OrderStatus;
import com.pos.domain.valueobject.PaymentMethod;
import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;
import com.pos.infrastructure.adapter.output.entity.SaleEntity;
import com.pos.infrastructure.adapter.output.entity.SaleItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Profile("!mock")
public class SaleJpaAdapter implements SaleRepository {

    private final SaleJpaRepository jpaRepository;

    public SaleJpaAdapter(SaleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Sale save(Sale sale) {
        SaleEntity entity = toEntity(sale);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<Sale> findAll(Pageable pageable, Instant from, Instant to, PaymentMethod paymentMethod) {
        String pm = paymentMethod != null ? paymentMethod.name() : null;
        return jpaRepository.findAllWithFilters(from, to, pm, pageable).map(this::toDomain);
    }

    @Override
    public Sale updateStatus(UUID id, OrderStatus status, Instant statusUpdatedAt) {
        SaleEntity entity = jpaRepository.findById(id)
            .orElseThrow(() -> new SaleNotFoundException(id));
        entity.setStatus(status.name());
        entity.setStatusUpdatedAt(statusUpdatedAt);
        return toDomain(jpaRepository.save(entity));
    }

    private Sale toDomain(SaleEntity e) {
        List<SaleItem> items = e.getItems().stream()
            .map(i -> new SaleItem(
                new SKU(i.getSku()), i.getProductName(),
                new Quantity(i.getQuantity()), new Money(i.getUnitPrice())
            ))
            .collect(Collectors.toList());

        return Sale.reconstitute(
            e.getId(), items,
            new Money(e.getSubtotal()), new Money(e.getDiscountAmount()),
            new Money(e.getTaxAmount()), new Money(e.getTotal()),
            PaymentMethod.valueOf(e.getPaymentMethod()),
            OrderStatus.valueOf(e.getStatus()),
            e.getCouponCode(), e.getCreatedAt(), e.getStatusUpdatedAt(), e.getCreatedBy()
        );
    }

    private SaleEntity toEntity(Sale s) {
        SaleEntity e = new SaleEntity();
        e.setId(s.getId());
        e.setSubtotal(s.getSubtotal().amount());
        e.setDiscountAmount(s.getDiscountAmount().amount());
        e.setTaxAmount(s.getTaxAmount().amount());
        e.setTotal(s.getTotal().amount());
        e.setPaymentMethod(s.getPaymentMethod().name());
        e.setStatus(s.getStatus().name());
        e.setCouponCode(s.getCouponCode());
        e.setCreatedAt(s.getCreatedAt());
        e.setStatusUpdatedAt(s.getStatusUpdatedAt());
        e.setCreatedBy(s.getCreatedBy());

        List<SaleItemEntity> itemEntities = s.getItems().stream().map(i -> {
            SaleItemEntity ie = new SaleItemEntity();
            ie.setSale(e);
            ie.setSku(i.getSku().value());
            ie.setProductName(i.getProductName());
            ie.setQuantity(i.getQuantity().value());
            ie.setUnitPrice(i.getUnitPrice().amount());
            ie.setSubtotal(i.getSubtotal().amount());
            return ie;
        }).collect(Collectors.toList());
        e.setItems(itemEntities);
        return e;
    }
}
