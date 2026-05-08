package com.pos.infrastructure.adapter.output.persistence;

import com.pos.infrastructure.adapter.output.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {

    Page<ProductEntity> findAllByActiveTrue(Pageable pageable);

    Page<ProductEntity> findAllByActiveTrueAndCategoryId(UUID categoryId, Pageable pageable);

    Optional<ProductEntity> findBySkuAndActiveTrue(String sku);

    boolean existsBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.sku = :sku")
    Optional<ProductEntity> findBySkuForUpdate(@Param("sku") String sku);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.stockQty = p.stockQty - :qty WHERE p.sku = :sku AND p.stockQty >= :qty")
    int decrementStock(@Param("sku") String sku, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE ProductEntity p SET p.stockQty = p.stockQty + :qty WHERE p.sku = :sku")
    int incrementStock(@Param("sku") String sku, @Param("qty") int qty);
}
