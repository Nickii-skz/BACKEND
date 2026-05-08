package com.pos.application.service;

import com.pos.application.port.input.SaleUseCase;
import com.pos.application.port.output.DiscountRepository;
import com.pos.application.port.output.InventoryPort;
import com.pos.application.port.output.ProductRepository;
import com.pos.application.port.output.SaleRepository;
import com.pos.domain.exception.CouponExhaustedException;
import com.pos.domain.exception.CouponNotFoundException;
import com.pos.domain.exception.InvalidCartException;
import com.pos.domain.exception.ProductNotFoundException;
import com.pos.domain.exception.SaleNotFoundException;
import com.pos.domain.model.Cart;
import com.pos.domain.model.CartItem;
import com.pos.domain.model.Discount;
import com.pos.domain.model.Product;
import com.pos.domain.model.Sale;
import com.pos.domain.model.SaleItem;
import com.pos.domain.model.SaleReceipt;
import com.pos.domain.valueobject.DiscountScope;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.PaymentMethod;
import com.pos.domain.valueobject.Quantity;
import com.pos.domain.valueobject.SKU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SaleService implements SaleUseCase {

    private static final Logger log = LoggerFactory.getLogger(SaleService.class);

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final InventoryPort inventoryPort;
    private final DiscountRepository discountRepository;

    @Value("${pos.tax.rate:0.19}")
    private BigDecimal taxRate;

    public SaleService(ProductRepository productRepository,
                       SaleRepository saleRepository,
                       InventoryPort inventoryPort,
                       DiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.inventoryPort = inventoryPort;
        this.discountRepository = discountRepository;
    }

    @Override
    public SaleReceipt createSale(Cart cart) {
        if (cart.isEmpty()) {
            throw new InvalidCartException("Cart must not be empty");
        }

        // 1. Resolve cart items to domain SaleItems
        List<SaleItem> saleItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getQuantity() <= 0) {
                throw new InvalidCartException("Quantity must be greater than zero for SKU: " + cartItem.getSku());
            }
            Product product = productRepository.findBySku(new SKU(cartItem.getSku()))
                .orElseThrow(() -> new ProductNotFoundException(cartItem.getSku()));
            saleItems.add(new SaleItem(
                product.getSku(),
                product.getName(),
                new Quantity(cartItem.getQuantity()),
                product.getUnitPrice()
            ));
        }

        // 2. Calculate subtotal
        Money subtotal = saleItems.stream()
            .map(SaleItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);

        // 3. Apply coupon if present
        Money discountAmount = Money.ZERO;
        String couponCode = null;
        if (cart.hasCoupon()) {
            final String finalCouponCode = cart.getCouponCode();
            couponCode = finalCouponCode;
            Discount discount = discountRepository.findByCouponCode(finalCouponCode)
                .orElseThrow(() -> new CouponNotFoundException(finalCouponCode));
            if (!discount.isValid()) {
                if (discount.isExhausted()) throw new CouponExhaustedException(finalCouponCode);
                throw new CouponExhaustedException(finalCouponCode); // expired or inactive
            }
            if (discount.getScope() == DiscountScope.PRODUCT && discount.getSku() != null) {
                final SKU discountSku = discount.getSku();
                // Apply only to matching items
                Money matchingSubtotal = saleItems.stream()
                    .filter(i -> i.getSku().equals(discountSku))
                    .map(SaleItem::getSubtotal)
                    .reduce(Money.ZERO, Money::add);
                discountAmount = discount.calculateDiscount(matchingSubtotal);
            } else {
                discountAmount = discount.calculateDiscount(subtotal);
            }
            discountRepository.incrementUsageCount(discount.getId());
        }

        // 4. Decrement stock atomically (inside @Transactional)
        for (CartItem cartItem : cart.getItems()) {
            inventoryPort.checkAndDecrementStock(new SKU(cartItem.getSku()), new Quantity(cartItem.getQuantity()));
        }

        // 5. Create and persist Sale
        Sale sale = Sale.create(saleItems, cart.getPaymentMethod(), discountAmount, taxRate, couponCode);
        Sale saved = saleRepository.save(sale);

        log.info("Sale created: id={}, total={}, paymentMethod={}", saved.getId(), saved.getTotal(), saved.getPaymentMethod());

        return SaleReceipt.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Sale> listSales(Pageable pageable, Instant from, Instant to, PaymentMethod paymentMethod) {
        return saleRepository.findAll(pageable, from, to, paymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public Sale getSaleById(UUID id) {
        return saleRepository.findById(id)
            .orElseThrow(() -> new SaleNotFoundException(id));
    }
}
