package com.pos.infrastructure.adapter.input.dto.response;

import com.pos.domain.valueobject.Money;

/**
 * DTO response for RefundItem.
 */
public record RefundItemResponse(
    String sku,
    String productName,
    int quantity,
    Money unitPrice,
    Money subtotal
) {}
