package com.pos.infrastructure.adapter.input.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Standard error response DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    UUID correlationId,
    List<FieldError> details
) {
    public record FieldError(String field, String message) {}

    public static ErrorResponse of(int status, String error, String message, UUID correlationId) {
        return new ErrorResponse(Instant.now(), status, error, message, correlationId, null);
    }

    public static ErrorResponse of(int status, String error, String message, UUID correlationId, List<FieldError> details) {
        return new ErrorResponse(Instant.now(), status, error, message, correlationId, details);
    }
}
