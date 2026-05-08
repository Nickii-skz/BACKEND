package com.pos.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing a monetary amount.
 * Always non-negative, scale=2, RoundingMode.HALF_UP.
 */
public record Money(BigDecimal amount) implements Comparable<Money> {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount, "Money amount must not be null");
        amount = amount.setScale(SCALE, ROUNDING);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount must not be negative: " + amount);
        }
    }

    public static Money of(String value) {
        return new Money(new BigDecimal(value));
    }

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money of(long cents) {
        return new Money(BigDecimal.valueOf(cents, SCALE));
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Operand must not be null");
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Operand must not be null");
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Money subtraction result must not be negative: " + this + " - " + other);
        }
        return new Money(result);
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Multiplication factor must not be negative: " + factor);
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "Factor must not be null");
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Multiplication factor must not be negative: " + factor);
        }
        return new Money(this.amount.multiply(factor).setScale(SCALE, ROUNDING));
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        Objects.requireNonNull(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
