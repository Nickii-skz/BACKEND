package com.pos.infrastructure.adapter.output.mock;

import com.pos.application.port.output.InventoryPort;
import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("mock")
public class InMemoryInventoryAdapter implements InventoryPort {

    private final Map<SKU, Integer> inventory = new ConcurrentHashMap<>();

    public InMemoryInventoryAdapter() {
        // Inventario inicial
        inventory.put(new SKU("LAPTOP-001"), 10);
        inventory.put(new SKU("MOUSE-001"), 50);
        inventory.put(new SKU("TSHIRT-001"), 100);
    }

    @Override
    public void checkAndDecrementStock(SKU sku, Quantity quantity) {
        inventory.compute(sku, (k, v) -> {
            int current = (v == null ? 0 : v);
            if (current < quantity.value()) {
                throw new RuntimeException("Insufficient stock for SKU: " + sku);
            }
            return current - quantity.value();
        });
    }

    @Override
    public void incrementStock(SKU sku, Quantity quantity) {
        inventory.compute(sku, (k, v) -> (v == null ? 0 : v) + quantity.value());
    }
}
