package com.pos.infrastructure.adapter.input.dto.request;

import com.pos.domain.model.Product;
import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.SKU;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductRequest {

    @NotBlank(message = "SKU must not be blank")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "unitPrice is required")
    @DecimalMin(value = "0.00", message = "unitPrice must be >= 0")
    private BigDecimal unitPrice;

    @Min(value = 0, message = "stockQuantity must be >= 0")
    private int stockQuantity;

    @NotNull(message = "categoryId is required")
    private UUID categoryId;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public Product toDomain() {
        return Product.create(
            new SKU(sku),
            name,
            description,
            imageUrl,
            new Money(unitPrice),
            stockQuantity,
            categoryId
        );
    }
}
