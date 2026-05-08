package com.pos.application.service;

import com.pos.application.port.input.ProductUseCase;
import com.pos.application.port.output.CategoryRepository;
import com.pos.application.port.output.ProductRepository;
import com.pos.domain.exception.CategoryNotFoundException;
import com.pos.domain.exception.DuplicateSkuException;
import com.pos.domain.exception.ProductNotFoundException;
import com.pos.domain.model.Product;
import com.pos.domain.valueobject.SKU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> listProducts(Pageable pageable, UUID categoryId) {
        return productRepository.findAll(pageable, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(new SKU(sku))
            .orElseThrow(() -> new ProductNotFoundException(sku));
    }

    @Override
    public Product createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new DuplicateSkuException(product.getSku().value());
        }
        if (!categoryRepository.existsById(product.getCategoryId())) {
            throw new CategoryNotFoundException(product.getCategoryId());
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String sku, Product updated) {
        Product existing = productRepository.findBySku(new SKU(sku))
            .orElseThrow(() -> new ProductNotFoundException(sku));
        if (!categoryRepository.existsById(updated.getCategoryId())) {
            throw new CategoryNotFoundException(updated.getCategoryId());
        }
        existing.update(updated.getName(), updated.getDescription(), updated.getImageUrl(),
                        updated.getUnitPrice(), updated.getStockQuantity(), updated.getCategoryId());
        return productRepository.save(existing);
    }

    @Override
    public void deactivateProduct(String sku) {
        Product product = productRepository.findBySku(new SKU(sku))
            .orElseThrow(() -> new ProductNotFoundException(sku));
        product.deactivate();
        productRepository.save(product);
    }
}
