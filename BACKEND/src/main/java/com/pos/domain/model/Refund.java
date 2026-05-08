package com.pos.domain.model;

import com.pos.domain.valueobject.Money;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a refund for a completed sale.
 */
public class Refund {

    private final UUID id;
    private final UUID saleId;
    private final List<RefundItem> items;
    private final Money totalRefunded;
    private final Instant createdAt;

    private Refund(UUID id, UUID saleId, List<RefundItem> items, Money totalRefunded, Instant createdAt) {
        this.id = id;
        this.saleId = saleId;
        this.items = Collections.unmodifiableList(items);
        this.totalRefunded = totalRefunded;
        this.createdAt = createdAt;
    }

    public static Refund create(UUID saleId, List<RefundItem> items, Money totalRefunded) {
        Objects.requireNonNull(saleId, "Sale ID must not be null");
        Objects.requireNonNull(items, "Items must not be null");
        Objects.requireNonNull(totalRefunded, "Total refunded must not be null");
        if (items.isEmpty()) throw new IllegalArgumentException("Refund must have at least one item");
        return new Refund(UUID.randomUUID(), saleId, items, totalRefunded, Instant.now());
    }

    public static Refund reconstitute(UUID id, UUID saleId, List<RefundItem> items,
                                      Money totalRefunded, Instant createdAt) {
        return new Refund(id, saleId, items, totalRefunded, createdAt);
    }

    public UUID getId() { return id; }
    public UUID getSaleId() { return saleId; }
    public List<RefundItem> getItems() { return items; }
    public Money getTotalRefunded() { return totalRefunded; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Refund r)) return false;
        return Objects.equals(id, r.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
