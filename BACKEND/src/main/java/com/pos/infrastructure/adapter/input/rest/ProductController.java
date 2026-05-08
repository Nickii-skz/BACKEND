package com.pos.infrastructure.adapter.input.rest;

import com.pos.application.port.input.ProductUseCase;
import com.pos.domain.model.Product;
import com.pos.infrastructure.adapter.input.dto.request.ProductRequest;
import com.pos.infrastructure.adapter.input.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product catalog management")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    @Operation(summary = "List products", description = "Returns paginated list of active products")
    public ResponseEntity<Page<ProductResponse>> listProducts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter by category ID") @RequestParam(required = false) UUID categoryId) {
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Product> products = productUseCase.listProducts(pageable, categoryId);
        
        Page<ProductResponse> response = products.map(this::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sku}")
    @Operation(summary = "Get product by SKU", description = "Returns a single product by its SKU")
    public ResponseEntity<ProductResponse> getProductBySku(
            @Parameter(description = "Product SKU") @PathVariable String sku) {
        
        Product product = productUseCase.getProductBySku(sku);
        return ResponseEntity.ok(toResponse(product));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create product", description = "Creates a new product (Admin only)")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = request.toDomain();
        Product created = productUseCase.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{sku}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product", description = "Updates an existing product (Admin only)")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product SKU") @PathVariable String sku,
            @Valid @RequestBody ProductRequest request) {
        
        Product product = request.toDomain();
        Product updated = productUseCase.updateProduct(sku, product);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{sku}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate product", description = "Soft deletes a product (Admin only)")
    public ResponseEntity<Void> deactivateProduct(
            @Parameter(description = "Product SKU") @PathVariable String sku) {
        
        productUseCase.deactivateProduct(sku);
        return ResponseEntity.noContent().build();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getSku().value(),
            product.getName(),
            product.getDescription(),
            product.getImageUrl(),
            product.getUnitPrice(),
            product.getStockQuantity(),
            product.isActive(),
            product.getCategoryId(),
            product.getCreatedAt(),
            product.getUpdatedAt(),
            product.getCreatedBy(),
            product.getUpdatedBy()
        );
    }
}
