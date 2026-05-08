package com.pos.infrastructure.adapter.output.persistence;

import com.pos.infrastructure.adapter.output.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findAllByActiveTrue();

    boolean existsByName(String name);

    @Query("SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.categoryId = :categoryId AND p.active = true")
    boolean hasActiveProducts(@Param("categoryId") UUID categoryId);
}
