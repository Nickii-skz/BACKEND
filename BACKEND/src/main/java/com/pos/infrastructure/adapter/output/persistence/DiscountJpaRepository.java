package com.pos.infrastructure.adapter.output.persistence;

import com.pos.infrastructure.adapter.output.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DiscountJpaRepository extends JpaRepository<DiscountEntity, UUID> {

    Optional<DiscountEntity> findByCouponCode(String couponCode);

    @Modifying
    @Query("UPDATE DiscountEntity d SET d.currentUsages = d.currentUsages + 1 WHERE d.id = :id")
    void incrementUsageCount(@Param("id") UUID id);
}
