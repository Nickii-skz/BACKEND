package com.pos.application.port.input;

import com.pos.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductUseCase {
    Page<Product> listProducts(Pageable pageable, UUID categoryId);
    Product getProductBySku(String sku);
    Product createProduct(Product product);
    Product updateProduct(String sku, Product product);
    void deactivateProduct(String sku);
}
