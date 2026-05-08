package com.pos.infrastructure.adapter.output.mock;

import com.pos.application.port.output.CategoryRepository;
import com.pos.domain.model.Category;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("mock")
public class InMemoryCategoryRepository implements CategoryRepository {

    private final Map<UUID, Category> storage = new ConcurrentHashMap<>();

    public InMemoryCategoryRepository() {
        // Datos de ejemplo
        Category electronics = Category.create("Electronics", "Electronic devices and accessories");
        Category clothing = Category.create("Clothing", "Apparel and fashion items");
        Category food = Category.create("Food & Beverages", "Food and drink products");
        
        storage.put(electronics.getId(), electronics);
        storage.put(clothing.getId(), clothing);
        storage.put(food.getId(), food);
    }

    @Override
    public Category save(Category category) {
        storage.put(category.getId(), category);
        return category;
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    @Override
    public boolean existsByName(String name) {
        return storage.values().stream()
            .anyMatch(c -> c.getName().equalsIgnoreCase(name));
    }

    @Override
    public boolean hasActiveProducts(UUID id) {
        // En modo mock, siempre retornamos false para permitir eliminar categorías
        return false;
    }
}
