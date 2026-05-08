package com.pos.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain entity representing a product category.
 */
public class Category {

    private final UUID id;
    private String name;
    private String description;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private Category(UUID id, String name, String description, boolean active,
                     Instant createdAt, Instant updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static Category create(String name, String description) {
        Objects.requireNonNull(name, "Category name must not be null");
        if (name.isBlank()) throw new IllegalArgumentException("Category name must not be blank");
        if (name.length() > 100) throw new IllegalArgumentException("Category name must not exceed 100 characters");
        Instant now = Instant.now();
        return new Category(UUID.randomUUID(), name, description, true, now, now, null, null);
    }

    public static Category reconstitute(UUID id, String name, String description, boolean active,
                                        Instant createdAt, Instant updatedAt,
                                        String createdBy, String updatedBy) {
        return new Category(id, name, description, active, createdAt, updatedAt, createdBy, updatedBy);
    }

    public void update(String name, String description) {
        Objects.requireNonNull(name, "Category name must not be null");
        if (name.isBlank()) throw new IllegalArgumentException("Category name must not be blank");
        if (name.length() > 100) throw new IllegalArgumentException("Category name must not exceed 100 characters");
        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category c)) return false;
        return Objects.equals(id, c.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "', active=" + active + "}";
    }
}
