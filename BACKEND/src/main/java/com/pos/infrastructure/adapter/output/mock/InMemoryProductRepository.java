package com.pos.infrastructure.adapter.output.mock;

import com.pos.application.port.output.ProductRepository;
import com.pos.domain.model.Product;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.SKU;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Profile("mock")
public class InMemoryProductRepository implements ProductRepository {

    private final Map<SKU, Product> storage = new ConcurrentHashMap<>();

    public InMemoryProductRepository() {
        // Datos de ejemplo
        UUID electronicsId = UUID.randomUUID();
        UUID clothingId = UUID.randomUUID();
        
        Product laptop = Product.create(
            new SKU("LAPTOP-001"),
            "Gaming Laptop",
            "High-performance gaming laptop",
            "https://example.com/laptop.jpg",
            new Money(new BigDecimal("1299.99")),
            10,
            electronicsId
        );
        
        Product mouse = Product.create(
            new SKU("MOUSE-001"),
            "Wireless Mouse",
            "Ergonomic wireless mouse",
            "https://example.com/mouse.jpg",
            new Money(new BigDecimal("29.99")),
            50,
            electronicsId
        );
        
        Product tshirt = Product.create(
            new SKU("TSHIRT-001"),
            "Cotton T-Shirt",
            "Comfortable cotton t-shirt",
            "https://example.com/tshirt.jpg",
            new Money(new BigDecimal("19.99")),
            100,
            clothingId
        );
        
        storage.put(laptop.getSku(), laptop);
        storage.put(mouse.getSku(), mouse);
        storage.put(tshirt.getSku(), tshirt);
    }

    @Override
    public Product save(Product product) {
        storage.put(product.getSku(), product);
        return product;
    }

    @Override
    public Optional<Product> findBySku(SKU sku) {
        return Optional.ofNullable(storage.get(sku));
    }

    @Override
    public Page<Product> findAll(Pageable pageable, UUID categoryId) {
        List<Product> filtered = storage.values().stream()
            .filter(p -> categoryId == null || p.getCategoryId().equals(categoryId))
            .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        if (start > filtered.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, filtered.size());
        }
        List<Product> page = filtered.subList(start, end);
        return new PageImpl<>(page, pageable, filtered.size());
    }

    @Override
    public boolean existsBySku(SKU sku) {
        return storage.containsKey(sku);
    }
}
