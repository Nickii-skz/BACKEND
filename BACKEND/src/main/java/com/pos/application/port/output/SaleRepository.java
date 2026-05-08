package com.pos.application.port.output;

import com.pos.domain.model.Sale;
import com.pos.domain.valueobject.OrderStatus;
import com.pos.domain.valueobject.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port: contract for Sale persistence.
 * Implemented by SaleJpaAdapter in the infrastructure layer.
 */
public interface SaleRepository {

    Sale save(Sale sale);

    Optional<Sale> findById(UUID id);

    Page<Sale> findAll(Pageable pageable, Instant from, Instant to, PaymentMethod paymentMethod);

    Sale updateStatus(UUID id, OrderStatus status, Instant statusUpdatedAt);
}
