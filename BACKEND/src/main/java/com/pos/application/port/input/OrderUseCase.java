package com.pos.application.port.input;

import com.pos.domain.model.Refund;
import com.pos.domain.model.RefundItem;
import com.pos.domain.model.Sale;
import com.pos.domain.valueobject.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderUseCase {
    Sale updateOrderStatus(UUID saleId, OrderStatus newStatus);
    Refund refundOrder(UUID saleId, List<RefundItem> items);
}
