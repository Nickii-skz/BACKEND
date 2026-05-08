package com.pos.infrastructure.adapter.input.dto.request;

import com.pos.domain.model.RefundItem;
import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RefundRequest(
    @NotEmpty(message = "Refund items must not be empty")
    @Valid
    List<RefundItemRequest> items
) {
    public List<RefundItem> toRefundItems() {
        return items.stream()
            .map(i -> new RefundItem(new SKU(i.getSku()), new Quantity(i.getQuantity())))
            .toList();
    }
}
