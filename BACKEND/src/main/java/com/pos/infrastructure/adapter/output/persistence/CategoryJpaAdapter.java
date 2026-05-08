package com.pos.infrastructure.adapter.output.persistence;

import com.pos.application.port.output.CategoryRepository;
import com.pos.domain.model.Category;
import com.pos.infrastructure.adapter.output.entity.CategoryEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Profile("!mock")
public class CategoryJpaAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    public CategoryJpaAdapter(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Cacheable("categories")
    public List<Category> findAll() {
        return jpaRepository.findAllByActiveTrue().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Category save(Category category) {
        return toDomain(jpaRepository.save(toEntity(category)));
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public boolean hasActiveProducts(UUID id) {
        return jpaRepository.hasActiveProducts(id);
    }

    private Category toDomain(CategoryEntity e) {
        return Category.reconstitute(e.getId(), e.getName(), e.getDescription(), e.isActive(),
            e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy());
    }

    private CategoryEntity toEntity(Category c) {
        CategoryEntity e = new CategoryEntity();
        e.setId(c.getId());
        e.setName(c.getName());
        e.setDescription(c.getDescription());
        e.setActive(c.isActive());
        e.setCreatedAt(c.getCreatedAt());
        e.setUpdatedAt(c.getUpdatedAt());
        e.setCreatedBy(c.getCreatedBy());
        e.setUpdatedBy(c.getUpdatedBy());
        return e;
    }
}
