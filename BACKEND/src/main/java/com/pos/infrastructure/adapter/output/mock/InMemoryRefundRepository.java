package com.pos.infrastructure.adapter.output.mock;

import com.pos.application.port.output.RefundRepository;
import com.pos.domain.model.Refund;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Profile("mock")
public class InMemoryRefundRepository implements RefundRepository {

    private final Map<UUID, Refund> storage = new ConcurrentHashMap<>();
    private final Map<UUID, List<UUID>> saleIndex = new ConcurrentHashMap<>();

    @Override
    public Refund save(Refund refund) {
        storage.put(refund.getId(), refund);
        saleIndex.computeIfAbsent(refund.getSaleId(), k -> new ArrayList<>()).add(refund.getId());
        return refund;
    }

    @Override
    public List<Refund> findBySaleId(UUID saleId) {
        List<UUID> refundIds = saleIndex.getOrDefault(saleId, Collections.emptyList());
        return refundIds.stream()
            .map(storage::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
