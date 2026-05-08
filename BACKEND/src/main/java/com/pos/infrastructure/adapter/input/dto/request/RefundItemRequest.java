package com.pos.infrastructure.adapter.input.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class RefundItemRequest {

    @NotBlank(message = "SKU must not be blank")
    private String sku;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
