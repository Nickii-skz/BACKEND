package com.pos.application.port.output;

import com.pos.domain.model.Refund;

import java.util.List;
import java.util.UUID;

/**
 * Output port: contract for Refund persistence.
 * Implemented by RefundJpaAdapter in the infrastructure layer.
 */
public interface RefundRepository {

    Refund save(Refund refund);

    List<Refund> findBySaleId(UUID saleId);
}
