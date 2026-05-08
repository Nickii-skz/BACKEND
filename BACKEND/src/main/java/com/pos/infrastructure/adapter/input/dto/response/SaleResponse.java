package com.pos.infrastructure.adapter.input.dto.response;

import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.OrderStatus;
import com.pos.domain.valueobject.PaymentMethod;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO response for Sale queries.
 */
public record SaleResponse(
    UUID id,
    List<SaleItemResponse> items,
    Money subtotal,
    Money discountAmount,
    Money taxAmount,
    Money total,
    PaymentMethod paymentMethod,
    OrderStatus status,
    String couponCode,
    Instant createdAt,
    Instant statusUpdatedAt,
    String createdBy
) {}
