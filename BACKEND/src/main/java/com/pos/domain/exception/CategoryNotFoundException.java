package com.pos.domain.exception;
import java.util.UUID;

public class CategoryNotFoundException extends PosException {
    private final UUID categoryId;
    public CategoryNotFoundException(UUID categoryId) {
        super("Category not found with ID: " + categoryId);
        this.categoryId = categoryId;
    }
    public UUID getCategoryId() { return categoryId; }
}
