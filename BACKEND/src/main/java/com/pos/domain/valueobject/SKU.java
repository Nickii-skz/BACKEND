package com.pos.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a Stock Keeping Unit identifier.
 * Immutable, validated on construction.
 */
public record SKU(String value) {

    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Za-z0-9\\-]{1,50}$");

    public SKU {
        Objects.requireNonNull(value, "SKU value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SKU value must not be blank");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("SKU value must not exceed 50 characters, got: " + value.length());
        }
        if (!VALID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "SKU value must be alphanumeric with hyphens only [A-Za-z0-9-]: " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
