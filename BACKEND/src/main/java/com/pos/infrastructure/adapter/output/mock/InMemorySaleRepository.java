package com.pos.infrastructure.adapter.output.mock;

import com.pos.application.port.output.SaleRepository;
import com.pos.domain.model.Sale;
import com.pos.domain.valueobject.OrderStatus;
import com.pos.domain.valueobject.PaymentMethod;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Profile("mock")
public class InMemorySaleRepository implements SaleRepository {

    private final Map<UUID, Sale> storage = new ConcurrentHashMap<>();

    @Override
    public Sale save(Sale sale) {
        storage.put(sale.getId(), sale);
        return sale;
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Page<Sale> findAll(Pageable pageable, Instant from, Instant to, PaymentMethod paymentMethod) {
        List<Sale> filtered = storage.values().stream()
            .filter(s -> from == null || !s.getCreatedAt().isBefore(from))
            .filter(s -> to == null || !s.getCreatedAt().isAfter(to))
            .filter(s -> paymentMethod == null || s.getPaymentMethod().equals(paymentMethod))
            .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        if (start > filtered.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, filtered.size());
        }
        List<Sale> page = filtered.subList(start, end);
        return new PageImpl<>(page, pageable, filtered.size());
    }

    @Override
    public Sale updateStatus(UUID id, OrderStatus status, Instant statusUpdatedAt) {
        Sale sale = storage.get(id);
        if (sale != null) {
            sale.transitionTo(status);
        }
        return sale;
    }
}
