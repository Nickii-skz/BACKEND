package com.pos.infrastructure.adapter.output.persistence;

import com.pos.infrastructure.adapter.output.entity.SaleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, UUID> {

    @Query("SELECT s FROM SaleEntity s WHERE " +
           "(:from IS NULL OR s.createdAt >= :from) AND " +
           "(:to IS NULL OR s.createdAt <= :to) AND " +
           "(:paymentMethod IS NULL OR s.paymentMethod = :paymentMethod)")
    Page<SaleEntity> findAllWithFilters(
        @Param("from") Instant from,
        @Param("to") Instant to,
        @Param("paymentMethod") String paymentMethod,
        Pageable pageable
    );
}
