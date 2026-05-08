package com.pos.application.port.input;

import com.pos.domain.model.Category;
import com.pos.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CategoryUseCase {
    List<Category> listCategories();
    Category createCategory(Category category);
    Category updateCategory(UUID id, Category category);
    void deactivateCategory(UUID id);
    Page<Product> getProductsByCategory(UUID categoryId, Pageable pageable);
}
