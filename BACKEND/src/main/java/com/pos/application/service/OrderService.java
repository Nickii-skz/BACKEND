package com.pos.application.service;

import com.pos.application.port.input.OrderUseCase;
import com.pos.application.port.output.InventoryPort;
import com.pos.application.port.output.RefundRepository;
import com.pos.application.port.output.SaleRepository;
import com.pos.domain.exception.InvalidCartException;
import com.pos.domain.exception.SaleNotFoundException;
import com.pos.domain.model.Refund;
import com.pos.domain.model.RefundItem;
import com.pos.domain.model.Sale;
import com.pos.domain.model.SaleItem;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.OrderStatus;
import com.pos.domain.valueobject.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService implements OrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final SaleRepository saleRepository;
    private final InventoryPort inventoryPort;
    private final RefundRepository refundRepository;

    public OrderService(SaleRepository saleRepository,
                        InventoryPort inventoryPort,
                        RefundRepository refundRepository) {
        this.saleRepository = saleRepository;
        this.inventoryPort = inventoryPort;
        this.refundRepository = refundRepository;
    }

    @Override
    public Sale updateOrderStatus(UUID saleId, OrderStatus newStatus) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new SaleNotFoundException(saleId));

        sale.transitionTo(newStatus); // throws InvalidOrderTransitionException if invalid

        // If cancelling, restore stock
        if (newStatus == OrderStatus.CANCELLED) {
            for (SaleItem item : sale.getItems()) {
                inventoryPort.incrementStock(item.getSku(), item.getQuantity());
            }
            log.info("Sale cancelled, stock restored: saleId={}", saleId);
        }

        return saleRepository.updateStatus(saleId, newStatus, Instant.now());
    }

    @Override
    public Refund refundOrder(UUID saleId, List<RefundItem> refundItems) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new SaleNotFoundException(saleId));

        if (sale.getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidCartException("Only DELIVERED sales can be refunded. Current status: " + sale.getStatus());
        }

        // Build a map of original quantities by SKU
        Map<String, Integer> originalQty = sale.getItems().stream()
            .collect(Collectors.toMap(i -> i.getSku().value(), i -> i.getQuantity().value()));

        // Validate refund quantities and calculate total refunded
        Money totalRefunded = Money.ZERO;
        for (RefundItem refundItem : refundItems) {
            String skuVal = refundItem.getSku().value();
            Integer original = originalQty.get(skuVal);
            if (original == null) {
                throw new InvalidCartException("SKU not found in original sale: " + skuVal);
            }
            if (refundItem.getQuantity().value() > original) {
                throw new InvalidCartException(
                    "Refund quantity " + refundItem.getQuantity().value() +
                    " exceeds original quantity " + original + " for SKU: " + skuVal);
            }
            // Find unit price from sale items
            Money unitPrice = sale.getItems().stream()
                .filter(i -> i.getSku().value().equals(skuVal))
                .findFirst()
                .map(SaleItem::getUnitPrice)
                .orElseThrow();
            totalRefunded = totalRefunded.add(unitPrice.multiply(refundItem.getQuantity().value()));
        }

        // Restore stock for refunded items
        for (RefundItem refundItem : refundItems) {
            inventoryPort.incrementStock(refundItem.getSku(), refundItem.getQuantity());
        }

        // Persist refund
        Refund refund = Refund.create(saleId, refundItems, totalRefunded);
        Refund saved = refundRepository.save(refund);

        log.info("Refund processed: saleId={}, refundId={}, totalRefunded={}", saleId, saved.getId(), totalRefunded);
        return saved;
    }
}
