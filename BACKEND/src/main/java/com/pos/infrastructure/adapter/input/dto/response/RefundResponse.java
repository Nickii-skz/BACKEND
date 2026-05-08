package com.pos.infrastructure.adapter.input.dto.response;

import com.pos.domain.valueobject.Money;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO response for Refund operations.
 */
public record RefundResponse(
    UUID id,
    UUID saleId,
    List<RefundItemResponse> items,
    Money totalRefunded,
    Instant createdAt
) {}
