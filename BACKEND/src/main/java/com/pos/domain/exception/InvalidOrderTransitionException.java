package com.pos.domain.exception;

import com.pos.domain.valueobject.OrderStatus;
import java.util.UUID;

public class InvalidOrderTransitionException extends PosException {
    private final OrderStatus currentStatus;
    private final OrderStatus requestedStatus;

    public InvalidOrderTransitionException(UUID saleId, OrderStatus currentStatus, OrderStatus requestedStatus) {
        super("Invalid order status transition for sale " + saleId +
              ": cannot transition from " + currentStatus + " to " + requestedStatus);
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
    }
    public OrderStatus getCurrentStatus() { return currentStatus; }
    public OrderStatus getRequestedStatus() { return requestedStatus; }
}
