package com.pos.infrastructure.adapter.output.persistence;

import com.pos.infrastructure.adapter.output.entity.RefundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RefundJpaRepository extends JpaRepository<RefundEntity, UUID> {

    List<RefundEntity> findBySaleId(UUID saleId);
}
