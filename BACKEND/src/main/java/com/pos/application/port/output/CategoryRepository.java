package com.pos.application.port.output;

import com.pos.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: contract for Category persistence.
 * Implemented by CategoryJpaAdapter in the infrastructure layer.
 */
public interface CategoryRepository {

    List<Category> findAll();

    Optional<Category> findById(UUID id);

    Category save(Category category);

    boolean existsById(UUID id);

    boolean existsByName(String name);

    /**
     * Returns true if the category has at least one active product associated.
     */
    boolean hasActiveProducts(UUID id);
}
