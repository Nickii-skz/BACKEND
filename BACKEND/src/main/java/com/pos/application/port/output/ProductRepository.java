package com.pos.application.port.output;

import com.pos.domain.model.Product;
import com.pos.domain.valueobject.SKU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port: contract for Product persistence.
 * Implemented by ProductJpaAdapter in the infrastructure layer.
 */
public interface ProductRepository {

    Page<Product> findAll(Pageable pageable, UUID categoryId);

    Optional<Product> findBySku(SKU sku);

    Product save(Product product);

    boolean existsBySku(SKU sku);
}
