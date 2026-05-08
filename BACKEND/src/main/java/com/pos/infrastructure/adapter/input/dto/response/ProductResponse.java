package com.pos.infrastructure.adapter.input.dto.response;

import com.pos.domain.valueobject.Money;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO response for Product queries.
 */
public record ProductResponse(
    String sku,
    String name,
    String description,
    String imageUrl,
    Money unitPrice,
    int stockQuantity,
    boolean active,
    UUID categoryId,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String updatedBy
) {}
