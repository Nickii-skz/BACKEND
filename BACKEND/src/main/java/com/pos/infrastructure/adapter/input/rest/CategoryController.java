package com.pos.infrastructure.adapter.input.rest;

import com.pos.application.port.input.CategoryUseCase;
import com.pos.application.port.input.ProductUseCase;
import com.pos.domain.model.Category;
import com.pos.domain.model.Product;
import com.pos.infrastructure.adapter.input.dto.request.CategoryRequest;
import com.pos.infrastructure.adapter.input.dto.response.CategoryResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Product category management")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryUseCase categoryUseCase;
    private final ProductUseCase productUseCase;

    public CategoryController(CategoryUseCase categoryUseCase, ProductUseCase productUseCase) {
        this.categoryUseCase = categoryUseCase;
        this.productUseCase = productUseCase;
    }

    @GetMapping
    @Operation(summary = "List categories", description = "Returns all active categories")
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        List<Category> categories = categoryUseCase.listCategories();
        List<CategoryResponse> response = categories.stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create category", description = "Creates a new category (Admin only)")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = request.toDomain();
        Category created = categoryUseCase.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a single category")
    public ResponseEntity<CategoryResponse> getCategory(
            @Parameter(description = "Category ID") @PathVariable UUID id) {
        // This would need a new method in CategoryUseCase, using listCategories for now
        List<Category> categories = categoryUseCase.listCategories();
        Category category = categories.stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        return ResponseEntity.ok(toResponse(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update category", description = "Updates an existing category (Admin only)")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category ID") @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        
        Category category = request.toDomain();
        Category updated = categoryUseCase.updateCategory(id, category);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate category", description = "Soft deletes a category (Admin only)")
    public ResponseEntity<Void> deactivateCategory(
            @Parameter(description = "Category ID") @PathVariable UUID id) {
        
        categoryUseCase.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "Get products by category", description = "Returns paginated products for a category")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @Parameter(description = "Category ID") @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Product> products = categoryUseCase.getProductsByCategory(id, pageable);
        
        Page<ProductResponse> response = products.map(this::toProductResponse);
        return ResponseEntity.ok(response);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.isActive(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }

    private ProductResponse toProductResponse(Product product) {
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
