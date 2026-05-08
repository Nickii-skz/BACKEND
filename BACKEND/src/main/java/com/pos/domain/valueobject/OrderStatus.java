package com.pos.domain.valueobject;

import java.util.Map;
import java.util.Set;

/**
 * Enum representing the lifecycle state of a Sale.
 * Encapsulates the valid state-machine transitions.
 *
 * Valid transitions:
 *   PENDING   → CONFIRMED, CANCELLED
 *   CONFIRMED → SHIPPED,   CANCELLED
 *   SHIPPED   → DELIVERED
 *   DELIVERED → (terminal)
 *   CANCELLED → (terminal)
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
        PENDING,   Set.of(CONFIRMED, CANCELLED),
        CONFIRMED, Set.of(SHIPPED, CANCELLED),
        SHIPPED,   Set.of(DELIVERED),
        DELIVERED, Set.of(),
        CANCELLED, Set.of()
    );

    /**
     * Returns true if transitioning from this status to {@code next} is allowed.
     */
    public boolean isValidTransitionTo(OrderStatus next) {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }

    /**
     * Returns true if this status is a terminal state (no further transitions possible).
     */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Returns the set of valid next statuses from this state.
     */
    public Set<OrderStatus> allowedTransitions() {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of());
    }
}
