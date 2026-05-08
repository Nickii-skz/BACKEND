package com.pos.application.service;

import com.pos.application.port.input.CategoryUseCase;
import com.pos.application.port.output.CategoryRepository;
import com.pos.application.port.output.ProductRepository;
import com.pos.domain.exception.CategoryNotFoundException;
import com.pos.domain.model.Category;
import com.pos.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryService implements CategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category name already exists: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(UUID id, Category updated) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));
        existing.update(updated.getName(), updated.getDescription());
        return categoryRepository.save(existing);
    }

    @Override
    public void deactivateCategory(UUID id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));
        if (categoryRepository.hasActiveProducts(id)) {
            throw new IllegalStateException("Cannot deactivate category with active products: " + id);
        }
        category.deactivate();
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(UUID categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        return productRepository.findAll(pageable, categoryId);
    }
}
