package com.pos.domain.valueobject;

/**
 * Value object representing a strictly positive integer quantity.
 */
public record Quantity(int value) {

    public Quantity {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero, got: " + value);
        }
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    public boolean isGreaterThan(Quantity other) {
        return this.value > other.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
