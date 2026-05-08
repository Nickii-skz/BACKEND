package com.pos.infrastructure.adapter.output.persistence;

import com.pos.application.port.output.InventoryPort;
import com.pos.domain.exception.InsufficientStockException;
import com.pos.domain.exception.ProductNotFoundException;
import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;
import com.pos.infrastructure.adapter.output.entity.ProductEntity;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!mock")
public class InventoryJpaAdapter implements InventoryPort {

    private final ProductJpaRepository jpaRepository;

    public InventoryJpaAdapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public void checkAndDecrementStock(SKU sku, Quantity quantity) {
        ProductEntity product = jpaRepository.findBySkuForUpdate(sku.value())
            .orElseThrow(() -> new ProductNotFoundException(sku.value()));

        if (product.getStockQty() < quantity.value()) {
            throw new InsufficientStockException(sku.value(), product.getStockQty(), quantity.value());
        }

        int updated = jpaRepository.decrementStock(sku.value(), quantity.value());
        if (updated == 0) {
            // Race condition: re-check
            ProductEntity reloaded = jpaRepository.findBySkuForUpdate(sku.value())
                .orElseThrow(() -> new ProductNotFoundException(sku.value()));
            throw new InsufficientStockException(sku.value(), reloaded.getStockQty(), quantity.value());
        }
    }

    @Override
    @Transactional
    public void incrementStock(SKU sku, Quantity quantity) {
        if (!jpaRepository.existsBySku(sku.value())) {
            throw new ProductNotFoundException(sku.value());
        }
        jpaRepository.incrementStock(sku.value(), quantity.value());
    }
}
