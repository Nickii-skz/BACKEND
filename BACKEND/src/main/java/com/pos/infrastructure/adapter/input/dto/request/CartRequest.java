package com.pos.infrastructure.adapter.input.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartRequest(
    @NotEmpty(message = "Cart must not be empty")
    @Valid
    List<SaleItemRequest> items,

    @NotNull(message = "paymentMethod is required")
    String paymentMethod,

    String couponCode
) {}
