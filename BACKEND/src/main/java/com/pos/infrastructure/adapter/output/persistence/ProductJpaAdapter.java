package com.pos.infrastructure.adapter.output.persistence;

import com.pos.application.port.output.ProductRepository;
import com.pos.domain.exception.ProductNotFoundException;
import com.pos.domain.model.Product;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.SKU;
import com.pos.infrastructure.adapter.output.entity.ProductEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.util.Optional;
import java.util.UUID;

@Component
@Profile("!mock")
public class ProductJpaAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    public ProductJpaAdapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<Product> findAll(Pageable pageable, UUID categoryId) {
        if (categoryId != null) {
            return jpaRepository.findAllByActiveTrueAndCategoryId(categoryId, pageable)
                .map(this::toDomain);
        }
        return jpaRepository.findAllByActiveTrue(pageable).map(this::toDomain);
    }

    @Override
    @Cacheable(value = "products", key = "#sku.value()")
    public Optional<Product> findBySku(SKU sku) {
        return jpaRepository.findBySkuAndActiveTrue(sku.value()).map(this::toDomain);
    }

    @Override
    @CacheEvict(value = {"products", "products-list"}, allEntries = true)
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existsBySku(SKU sku) {
        return jpaRepository.existsBySku(sku.value());
    }

    private Product toDomain(ProductEntity e) {
        return Product.reconstitute(
            new SKU(e.getSku()), e.getName(), e.getDescription(), e.getImageUrl(),
            new Money(e.getUnitPrice()), e.getStockQty(), e.isActive(), e.getCategoryId(),
            e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy()
        );
    }

    private ProductEntity toEntity(Product p) {
        ProductEntity e = new ProductEntity();
        e.setSku(p.getSku().value());
        e.setName(p.getName());
        e.setDescription(p.getDescription());
        e.setImageUrl(p.getImageUrl());
        e.setUnitPrice(p.getUnitPrice().amount());
        e.setStockQty(p.getStockQuantity());
        e.setActive(p.isActive());
        e.setCategoryId(p.getCategoryId());
        e.setCreatedAt(p.getCreatedAt());
        e.setUpdatedAt(p.getUpdatedAt());
        e.setCreatedBy(p.getCreatedBy());
        e.setUpdatedBy(p.getUpdatedBy());
        return e;
    }
}
