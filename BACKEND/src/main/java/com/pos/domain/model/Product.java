package com.pos.domain.model;

import com.pos.domain.valueobject.Money;
import com.pos.domain.valueobject.SKU;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a product in the catalog.
 */
public class Product {

    private final SKU sku;
    private String name;
    private String description;
    private String imageUrl;
    private Money unitPrice;
    private int stockQuantity;
    private boolean active;
    private UUID categoryId;
    private final Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private Product(SKU sku, String name, String description, String imageUrl,
                    Money unitPrice, int stockQuantity, boolean active, UUID categoryId,
                    Instant createdAt, Instant updatedAt, String createdBy, String updatedBy) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.active = active;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static Product create(SKU sku, String name, String description, String imageUrl,
                                 Money unitPrice, int stockQuantity, UUID categoryId) {
        Objects.requireNonNull(sku, "SKU must not be null");
        Objects.requireNonNull(name, "Product name must not be null");
        Objects.requireNonNull(unitPrice, "Unit price must not be null");
        Objects.requireNonNull(categoryId, "Category ID must not be null");
        if (name.isBlank()) throw new IllegalArgumentException("Product name must not be blank");
        if (name.length() > 255) throw new IllegalArgumentException("Product name must not exceed 255 characters");
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock quantity must not be negative");
        validateImageUrl(imageUrl);
        Instant now = Instant.now();
        return new Product(sku, name, description, imageUrl, unitPrice, stockQuantity, true,
                           categoryId, now, now, null, null);
    }

    public static Product reconstitute(SKU sku, String name, String description, String imageUrl,
                                       Money unitPrice, int stockQuantity, boolean active, UUID categoryId,
                                       Instant createdAt, Instant updatedAt,
                                       String createdBy, String updatedBy) {
        return new Product(sku, name, description, imageUrl, unitPrice, stockQuantity, active,
                           categoryId, createdAt, updatedAt, createdBy, updatedBy);
    }

    public void update(String name, String description, String imageUrl,
                       Money unitPrice, int stockQuantity, UUID categoryId) {
        Objects.requireNonNull(name, "Product name must not be null");
        Objects.requireNonNull(unitPrice, "Unit price must not be null");
        Objects.requireNonNull(categoryId, "Category ID must not be null");
        if (name.isBlank()) throw new IllegalArgumentException("Product name must not be blank");
        if (name.length() > 255) throw new IllegalArgumentException("Product name must not exceed 255 characters");
        if (stockQuantity < 0) throw new IllegalArgumentException("Stock quantity must not be negative");
        validateImageUrl(imageUrl);
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    private static void validateImageUrl(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank() && !imageUrl.startsWith("https://")) {
            throw new IllegalArgumentException("imageUrl must start with https://: " + imageUrl);
        }
    }

    // Getters
    public SKU getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public Money getUnitPrice() { return unitPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public boolean isActive() { return active; }
    public UUID getCategoryId() { return categoryId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product p)) return false;
        return Objects.equals(sku, p.sku);
    }

    @Override
    public int hashCode() { return Objects.hash(sku); }

    @Override
    public String toString() {
        return "Product{sku=" + sku + ", name='" + name + "', active=" + active + "}";
    }
}
