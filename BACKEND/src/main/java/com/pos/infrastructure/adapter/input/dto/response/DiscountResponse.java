package com.pos.infrastructure.adapter.input.dto.response;

import com.pos.domain.valueobject.DiscountScope;
import com.pos.domain.valueobject.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO response for Discount queries.
 */
public record DiscountResponse(
    UUID id,
    String couponCode,
    DiscountType type,
    BigDecimal value,
    DiscountScope scope,
    String sku,
    int maxUsages,
    int currentUsages,
    Instant expiresAt,
    boolean active,
    Instant createdAt
) {}
