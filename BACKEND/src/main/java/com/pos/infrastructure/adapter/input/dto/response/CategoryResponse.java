package com.pos.infrastructure.adapter.input.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO response for Category queries.
 */
public record CategoryResponse(
    UUID id,
    String name,
    String description,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
