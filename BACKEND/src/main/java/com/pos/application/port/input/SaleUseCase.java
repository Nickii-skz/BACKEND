package com.pos.application.port.input;

import com.pos.domain.model.Cart;
import com.pos.domain.model.Sale;
import com.pos.domain.model.SaleReceipt;
import com.pos.domain.valueobject.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface SaleUseCase {
    SaleReceipt createSale(Cart cart);
    Page<Sale> listSales(Pageable pageable, Instant from, Instant to, PaymentMethod paymentMethod);
    Sale getSaleById(UUID id);
}
