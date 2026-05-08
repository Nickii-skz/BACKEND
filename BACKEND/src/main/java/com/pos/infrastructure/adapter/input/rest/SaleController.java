package com.pos.infrastructure.adapter.input.rest;

import com.pos.application.port.input.OrderUseCase;
import com.pos.application.port.input.SaleUseCase;
import com.pos.domain.model.Cart;
import com.pos.domain.model.CartItem;
import com.pos.domain.model.Refund;
import com.pos.domain.model.Sale;
import com.pos.domain.valueobject.OrderStatus;
import com.pos.domain.valueobject.PaymentMethod;
import com.pos.infrastructure.adapter.input.dto.request.CartRequest;
import com.pos.infrastructure.adapter.input.dto.request.OrderStatusRequest;
import com.pos.infrastructure.adapter.input.dto.request.RefundRequest;
import com.pos.infrastructure.adapter.input.dto.response.PagedResponse;
import com.pos.infrastructure.adapter.input.dto.response.RefundResponse;
import com.pos.infrastructure.adapter.input.dto.response.SaleItemResponse;
import com.pos.infrastructure.adapter.input.dto.response.SaleReceiptResponse;
import com.pos.infrastructure.adapter.input.dto.response.SaleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
@Tag(name = "Sales", description = "Sale transaction management")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleUseCase saleUseCase;
    private final OrderUseCase orderUseCase;

    public SaleController(SaleUseCase saleUseCase, OrderUseCase orderUseCase) {
        this.saleUseCase = saleUseCase;
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @Operation(summary = "Create sale", description = "Registers a new sale transaction")
    public ResponseEntity<SaleReceiptResponse> createSale(@Valid @RequestBody CartRequest request) {
        Cart cart = toCart(request);
        var receipt = saleUseCase.createSale(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(toReceiptResponse(receipt));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List sales", description = "Returns paginated list of sales (Admin only)")
    public ResponseEntity<PagedResponse<SaleResponse>> listSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter from date (ISO-8601)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @Parameter(description = "Filter to date (ISO-8601)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @Parameter(description = "Filter by payment method") 
            @RequestParam(required = false) PaymentMethod paymentMethod) {
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Sale> sales = saleUseCase.listSales(pageable, from, to, paymentMethod);
        
        PagedResponse<SaleResponse> response = PagedResponse.of(
            sales.map(this::toResponse).getContent(),
            sales.getTotalElements(),
            sales.getTotalPages(),
            sales.getNumber(),
            sales.getSize()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID", description = "Returns a single sale by its ID")
    public ResponseEntity<SaleResponse> getSaleById(
            @Parameter(description = "Sale ID") @PathVariable UUID id) {
        
        Sale sale = saleUseCase.getSaleById(id);
        return ResponseEntity.ok(toResponse(sale));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Transitions the order to a new status (Admin only)")
    public ResponseEntity<SaleResponse> updateOrderStatus(
            @Parameter(description = "Sale ID") @PathVariable UUID id,
            @Valid @RequestBody OrderStatusRequest request) {
        
        OrderStatus newStatus = OrderStatus.valueOf(request.status());
        Sale sale = orderUseCase.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(toResponse(sale));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refund sale", description = "Processes a full or partial refund (Admin only)")
    public ResponseEntity<RefundResponse> refundSale(
            @Parameter(description = "Sale ID") @PathVariable UUID id,
            @Valid @RequestBody RefundRequest request) {
        
        Refund refund = orderUseCase.refundOrder(id, request.toRefundItems());
        return ResponseEntity.status(HttpStatus.CREATED).body(toRefundResponse(refund));
    }

    private Cart toCart(CartRequest request) {
        List<CartItem> items = request.items().stream()
            .map(i -> new CartItem(i.sku(), i.quantity()))
            .toList();
        PaymentMethod method = PaymentMethod.valueOf(request.paymentMethod());
        return new Cart(items, method, request.couponCode());
    }

    private SaleReceiptResponse toReceiptResponse(com.pos.domain.model.SaleReceipt receipt) {
        List<SaleItemResponse> items = receipt.items().stream()
            .map(i -> new SaleItemResponse(
                i.getSku().value(),
                i.getProductName(),
                i.getQuantity().value(),
                i.getUnitPrice(),
                i.getSubtotal()
            ))
            .toList();
        
        return new SaleReceiptResponse(
            receipt.saleId(),
            items,
            receipt.subtotal(),
            receipt.discountAmount(),
            receipt.taxAmount(),
            receipt.total(),
            receipt.paymentMethod(),
            receipt.status(),
            receipt.couponCode(),
            receipt.itemCount(),
            receipt.createdAt()
        );
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> items = sale.getItems().stream()
            .map(i -> new SaleItemResponse(
                i.getSku().value(),
                i.getProductName(),
                i.getQuantity().value(),
                i.getUnitPrice(),
                i.getSubtotal()
            ))
            .toList();
        
        return new SaleResponse(
            sale.getId(),
            items,
            sale.getSubtotal(),
            sale.getDiscountAmount(),
            sale.getTaxAmount(),
            sale.getTotal(),
            sale.getPaymentMethod(),
            sale.getStatus(),
            sale.getCouponCode(),
            sale.getCreatedAt(),
            sale.getStatusUpdatedAt(),
            sale.getCreatedBy()
        );
    }

    private RefundResponse toRefundResponse(Refund refund) {
        var items = refund.getItems().stream()
            .map(i -> new com.pos.infrastructure.adapter.input.dto.response.RefundItemResponse(
                i.getSku().value(),
                i.getProductName(),
                i.getQuantity().value(),
                i.getUnitPrice(),
                i.getSubtotal()
            ))
            .toList();
        
        return new RefundResponse(
            refund.getId(),
            refund.getSaleId(),
            items,
            refund.getTotalRefunded(),
            refund.getCreatedAt()
        );
    }
}
