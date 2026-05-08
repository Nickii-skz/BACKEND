package com.pos.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusRequest(
    @NotBlank(message = "status is required")
    String status
) {
}
