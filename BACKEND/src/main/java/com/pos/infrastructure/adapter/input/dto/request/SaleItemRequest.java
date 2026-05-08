package com.pos.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SaleItemRequest(
    @NotBlank(message = "SKU must not be blank")
    String sku,

    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity
) {}
