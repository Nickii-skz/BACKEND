package com.pos.infrastructure.adapter.output.persistence;

import com.pos.application.port.output.RefundRepository;
import com.pos.domain.model.Refund;
import com.pos.domain.model.RefundItem;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;
import com.pos.infrastructure.adapter.output.entity.RefundEntity;
import com.pos.infrastructure.adapter.output.entity.RefundItemEntity;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Profile("!mock")
public class RefundJpaAdapter implements RefundRepository {

    private final RefundJpaRepository jpaRepository;

    public RefundJpaAdapter(RefundJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Refund save(Refund refund) {
        return toDomain(jpaRepository.save(toEntity(refund)));
    }

    @Override
    public List<Refund> findBySaleId(UUID saleId) {
        return jpaRepository.findBySaleId(saleId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    private Refund toDomain(RefundEntity e) {
        List<RefundItem> items = e.getItems().stream()
            .map(i -> new RefundItem(new SKU(i.getSku()), new Quantity(i.getQuantity())))
            .collect(Collectors.toList());
        return Refund.reconstitute(e.getId(), e.getSaleId(), items,
            new Money(e.getTotalRefunded()), e.getCreatedAt());
    }

    private RefundEntity toEntity(Refund r) {
        RefundEntity e = new RefundEntity();
        e.setId(r.getId());
        e.setSaleId(r.getSaleId());
        e.setTotalRefunded(r.getTotalRefunded().amount());
        e.setCreatedAt(r.getCreatedAt());
        List<RefundItemEntity> items = r.getItems().stream().map(i -> {
            RefundItemEntity ie = new RefundItemEntity();
            ie.setRefund(e);
            ie.setSku(i.getSku().value());
            ie.setQuantity(i.getQuantity().value());
            return ie;
        }).collect(Collectors.toList());
        e.setItems(items);
        return e;
    }
}
