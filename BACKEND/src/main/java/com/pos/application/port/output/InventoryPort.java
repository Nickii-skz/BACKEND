package com.pos.application.port.output;

import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;

/**
 * Output port: contract for inventory stock management.
 * Implemented by InventoryJpaAdapter using pessimistic locking (SELECT FOR UPDATE).
 */
public interface InventoryPort {

    /**
     * Verifies that the product has sufficient stock and decrements it atomically.
     * Uses SELECT ... FOR UPDATE to prevent race conditions.
     *
     * @throws com.pos.domain.exception.InsufficientStockException if stock < quantity
     * @throws com.pos.domain.exception.ProductNotFoundException    if SKU does not exist
     */
    void checkAndDecrementStock(SKU sku, Quantity quantity);

    /**
     * Increments the stock for a product.
     * Used during order cancellations and refunds.
     *
     * @throws com.pos.domain.exception.ProductNotFoundException if SKU does not exist
     */
    void incrementStock(SKU sku, Quantity quantity);
}
