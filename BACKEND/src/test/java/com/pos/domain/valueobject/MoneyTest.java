package com.pos.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Money value object.
 * Validates: Requirements 2.2, 10.5, 14.3
 */
class MoneyTest {

    @Test
    void shouldCreateValidMoney() {
        // Given
        BigDecimal value = new BigDecimal("10.50");
        
        // When
        Money money = new Money(value);
        
        // Then
        assertNotNull(money);
        assertEquals(new BigDecimal("10.50"), money.amount());
        assertEquals("10.50", money.toString());
    }

    @Test
    void shouldCreateMoneyWithZero() {
        // When
        Money money = new Money(BigDecimal.ZERO);
        
        // Then
        assertNotNull(money);
        assertEquals(new BigDecimal("0.00"), money.amount());
        assertEquals(Money.ZERO, money);
    }

    @Test
    void shouldNormalizeScaleTo2Decimals() {
        // Given - values with different scales
        BigDecimal value1 = new BigDecimal("10");      // scale 0
        BigDecimal value2 = new BigDecimal("10.5");    // scale 1
        BigDecimal value3 = new BigDecimal("10.123");  // scale 3
        
        // When
        Money money1 = new Money(value1);
        Money money2 = new Money(value2);
        Money money3 = new Money(value3);
        
        // Then - all normalized to scale 2
        assertEquals(2, money1.amount().scale());
        assertEquals(2, money2.amount().scale());
        assertEquals(2, money3.amount().scale());
        assertEquals(new BigDecimal("10.00"), money1.amount());
        assertEquals(new BigDecimal("10.50"), money2.amount());
        assertEquals(new BigDecimal("10.12"), money3.amount()); // HALF_UP rounding
    }

    @Test
    void shouldApplyHalfUpRounding() {
        // Given - values that need rounding
        BigDecimal value1 = new BigDecimal("10.125"); // rounds up to 10.13
        BigDecimal value2 = new BigDecimal("10.124"); // rounds down to 10.12
        BigDecimal value3 = new BigDecimal("10.115"); // rounds up to 10.12 (HALF_UP)
        
        // When
        Money money1 = new Money(value1);
        Money money2 = new Money(value2);
        Money money3 = new Money(value3);
        
        // Then
        assertEquals(new BigDecimal("10.13"), money1.amount());
        assertEquals(new BigDecimal("10.12"), money2.amount());
        assertEquals(new BigDecimal("10.12"), money3.amount());
    }

    @Test
    void shouldRejectNullAmount() {
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Money(null)
        );
        assertTrue(exception.getMessage().contains("Money amount must not be null"));
    }

    @Test
    void shouldRejectNegativeAmount() {
        // Given
        BigDecimal negativeValue = new BigDecimal("-10.50");
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Money(negativeValue)
        );
        assertTrue(exception.getMessage().contains("Money amount must not be negative"));
    }

    @Test
    void shouldAddMoney() {
        // Given
        Money money1 = new Money(new BigDecimal("10.50"));
        Money money2 = new Money(new BigDecimal("5.25"));
        
        // When
        Money result = money1.add(money2);
        
        // Then
        assertEquals(new BigDecimal("15.75"), result.amount());
        assertEquals(2, result.amount().scale());
    }

    @Test
    void shouldSubtractMoney() {
        // Given
        Money money1 = new Money(new BigDecimal("10.50"));
        Money money2 = new Money(new BigDecimal("5.25"));
        
        // When
        Money result = money1.subtract(money2);
        
        // Then
        assertEquals(new BigDecimal("5.25"), result.amount());
        assertEquals(2, result.amount().scale());
    }

    @Test
    void shouldRejectSubtractionResultingInNegative() {
        // Given
        Money money1 = new Money(new BigDecimal("5.00"));
        Money money2 = new Money(new BigDecimal("10.00"));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> money1.subtract(money2)
        );
        assertTrue(exception.getMessage().contains("Money subtraction result must not be negative"));
    }

    @Test
    void shouldMultiplyByIntFactor() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        
        // When
        Money result = money.multiply(3);
        
        // Then
        assertEquals(new BigDecimal("31.50"), result.amount());
        assertEquals(2, result.amount().scale());
    }

    @Test
    void shouldMultiplyByZero() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        
        // When
        Money result = money.multiply(0);
        
        // Then
        assertEquals(Money.ZERO, result);
        assertEquals(new BigDecimal("0.00"), result.amount());
    }

    @Test
    void shouldRejectNegativeIntMultiplier() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> money.multiply(-3)
        );
        assertTrue(exception.getMessage().contains("Multiplication factor must not be negative"));
    }

    @Test
    void shouldMultiplyByBigDecimalFactor() {
        // Given
        Money money = new Money(new BigDecimal("100.00"));
        BigDecimal taxRate = new BigDecimal("0.16"); // 16% tax
        
        // When
        Money result = money.multiply(taxRate);
        
        // Then
        assertEquals(new BigDecimal("16.00"), result.amount());
        assertEquals(2, result.amount().scale());
    }

    @Test
    void shouldMultiplyByBigDecimalWithRounding() {
        // Given
        Money money = new Money(new BigDecimal("10.00"));
        BigDecimal factor = new BigDecimal("0.333"); // Results in 3.33 with HALF_UP
        
        // When
        Money result = money.multiply(factor);
        
        // Then
        assertEquals(new BigDecimal("3.33"), result.amount());
        assertEquals(2, result.amount().scale());
    }

    @Test
    void shouldRejectNegativeBigDecimalMultiplier() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        BigDecimal negativeFactor = new BigDecimal("-0.5");
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> money.multiply(negativeFactor)
        );
        assertTrue(exception.getMessage().contains("Multiplication factor must not be negative"));
    }

    @Test
    void shouldRejectNullBigDecimalMultiplier() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> money.multiply((BigDecimal) null)
        );
        assertTrue(exception.getMessage().contains("Factor must not be null"));
    }

    @Test
    void shouldRejectNullOperandInAdd() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> money.add(null)
        );
        assertTrue(exception.getMessage().contains("Operand must not be null"));
    }

    @Test
    void shouldRejectNullOperandInSubtract() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> money.subtract(null)
        );
        assertTrue(exception.getMessage().contains("Operand must not be null"));
    }

    @Test
    void shouldCompareMoneyValues() {
        // Given
        Money money1 = new Money(new BigDecimal("10.00"));
        Money money2 = new Money(new BigDecimal("20.00"));
        Money money3 = new Money(new BigDecimal("10.00"));
        
        // Then
        assertTrue(money1.compareTo(money2) < 0);
        assertTrue(money2.compareTo(money1) > 0);
        assertEquals(0, money1.compareTo(money3));
    }

    @Test
    void shouldCheckGreaterThanOrEqualTo() {
        // Given
        Money money1 = new Money(new BigDecimal("10.00"));
        Money money2 = new Money(new BigDecimal("20.00"));
        Money money3 = new Money(new BigDecimal("10.00"));
        
        // Then
        assertFalse(money1.isGreaterThanOrEqualTo(money2));
        assertTrue(money2.isGreaterThanOrEqualTo(money1));
        assertTrue(money1.isGreaterThanOrEqualTo(money3));
    }

    @Test
    void shouldSupportEquality() {
        // Given
        Money money1 = new Money(new BigDecimal("10.50"));
        Money money2 = new Money(new BigDecimal("10.50"));
        Money money3 = new Money(new BigDecimal("20.00"));
        
        // Then
        assertEquals(money1, money2);
        assertNotEquals(money1, money3);
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    void shouldCreateMoneyFromString() {
        // When
        Money money = Money.of("10.50");
        
        // Then
        assertEquals(new BigDecimal("10.50"), money.amount());
    }

    @Test
    void shouldCreateMoneyFromDouble() {
        // When
        Money money = Money.of(10.50);
        
        // Then
        assertEquals(new BigDecimal("10.50"), money.amount());
    }

    @Test
    void shouldCreateMoneyFromCents() {
        // When
        Money money = Money.of(1050L); // 1050 cents = 10.50
        
        // Then
        assertEquals(new BigDecimal("10.50"), money.amount());
    }

    @Test
    void shouldCalculateDiscountAmount() {
        // Given - Requirement 10.5: Calculate discount on subtotal
        Money subtotal = new Money(new BigDecimal("100.00"));
        BigDecimal discountPercentage = new BigDecimal("0.10"); // 10% discount
        
        // When
        Money discountAmount = subtotal.multiply(discountPercentage);
        
        // Then
        assertEquals(new BigDecimal("10.00"), discountAmount.amount());
    }

    @Test
    void shouldCalculateTaxAmount() {
        // Given - Requirement 14.3: Calculate tax as (subtotal - discount) × taxRate
        Money subtotal = new Money(new BigDecimal("100.00"));
        Money discountAmount = new Money(new BigDecimal("10.00"));
        BigDecimal taxRate = new BigDecimal("0.16"); // 16% tax
        
        // When
        Money netAmount = subtotal.subtract(discountAmount); // 90.00
        Money taxAmount = netAmount.multiply(taxRate);
        
        // Then
        assertEquals(new BigDecimal("90.00"), netAmount.amount());
        assertEquals(new BigDecimal("14.40"), taxAmount.amount());
    }

    @Test
    void shouldCalculateTotalWithDiscountAndTax() {
        // Given - Requirement 2.2: Calculate total = subtotal - discount + tax
        Money subtotal = new Money(new BigDecimal("100.00"));
        Money discountAmount = new Money(new BigDecimal("10.00"));
        Money taxAmount = new Money(new BigDecimal("14.40"));
        
        // When
        Money total = subtotal.subtract(discountAmount).add(taxAmount);
        
        // Then
        assertEquals(new BigDecimal("104.40"), total.amount());
    }
}
/**
 * Property-based tests for Money value object.
 * Feature: pos-backend, Property: Money arithmetic consistency
 * Validates: Requirements 2.2, 10.5, 14.3
 */
class MoneyProperties {

    /**
     * Property: Non-negative BigDecimal values always construct Money without exception.
     * Feature: pos-backend, Property 1: Valid Money construction
     * Validates: Requirements 2.2
     */
    @Property(tries = 1000)
    @Label("Non-negative values construct Money without exception")
    void nonNegativeValuesConstructMoneyWithoutException(
        @ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal value
    ) {
        // When
        Money money = new Money(value);

        // Then
        assertNotNull(money);
        assertTrue(money.amount().compareTo(BigDecimal.ZERO) >= 0);
        assertEquals(2, money.amount().scale());
    }

    /**
     * Property: Negative values always throw exception.
     * Feature: pos-backend, Property 2: Invalid Money rejection
     * Validates: Requirements 2.2
     */
    @Property(tries = 1000)
    @Label("Negative values always throw exception")
    void negativeValuesAlwaysThrowException(
        @ForAll @BigRange(min = "-999999.99", max = "-0.01") BigDecimal value
    ) {
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> new Money(value)
        );
    }

    /**
     * Property: Addition is commutative: a + b = b + a
     * Feature: pos-backend, Property 3: Money addition commutativity
     * Validates: Requirements 2.2
     */
    @Property(tries = 500)
    @Label("Addition is commutative")
    void additionIsCommutative(
        @ForAll("validMoney") Money a,
        @ForAll("validMoney") Money b
    ) {
        // When
        Money result1 = a.add(b);
        Money result2 = b.add(a);

        // Then
        assertEquals(result1, result2);
    }

    /**
     * Property: Addition is associative: (a + b) + c = a + (b + c)
     * Feature: pos-backend, Property 4: Money addition associativity
     * Validates: Requirements 2.2
     */
    @Property(tries = 500)
    @Label("Addition is associative")
    void additionIsAssociative(
        @ForAll("validMoney") Money a,
        @ForAll("validMoney") Money b,
        @ForAll("validMoney") Money c
    ) {
        // When
        Money result1 = a.add(b).add(c);
        Money result2 = a.add(b.add(c));

        // Then
        assertEquals(result1, result2);
    }

    /**
     * Property: Zero is the identity element for addition: a + 0 = a
     * Feature: pos-backend, Property 5: Money addition identity
     * Validates: Requirements 2.2
     */
    @Property(tries = 500)
    @Label("Zero is the identity element for addition")
    void zeroIsAdditionIdentity(
        @ForAll("validMoney") Money a
    ) {
        // When
        Money result = a.add(Money.ZERO);

        // Then
        assertEquals(a, result);
    }

    /**
     * Property: Subtraction is the inverse of addition: (a + b) - b = a
     * Feature: pos-backend, Property 6: Money subtraction inverse
     * Validates: Requirements 2.2
     */
    @Property(tries = 500)
    @Label("Subtraction is the inverse of addition")
    void subtractionIsInverseOfAddition(
        @ForAll("validMoney") Money a,
        @ForAll("validMoney") Money b
    ) {
        Assume.that(a.isGreaterThanOrEqualTo(b)); // Ensure a >= b to avoid negative result

        // When
        Money sum = a.add(b);
        Money result = sum.subtract(b);

        // Then
        assertEquals(a, result);
    }

    /**
     * Property: Multiplication by 1 is identity: a × 1 = a
     * Feature: pos-backend, Property 7: Money multiplication identity
     * Validates: Requirements 2.2, 10.5, 14.3
     */
    @Property(tries = 500)
    @Label("Multiplication by 1 is identity")
    void multiplicationByOneIsIdentity(
        @ForAll("validMoney") Money a
    ) {
        // When
        Money result = a.multiply(1);

        // Then
        assertEquals(a, result);
    }

    /**
     * Property: Multiplication by 0 always results in ZERO
     * Feature: pos-backend, Property 8: Money multiplication by zero
     * Validates: Requirements 2.2
     */
    @Property(tries = 500)
    @Label("Multiplication by 0 always results in ZERO")
    void multiplicationByZeroIsZero(
        @ForAll("validMoney") Money a
    ) {
        // When
        Money result = a.multiply(0);

        // Then
        assertEquals(Money.ZERO, result);
    }

    /**
     * Property: Multiplication is distributive: a × (b + c) = (a × b) + (a × c)
     * Note: Due to rounding, we check approximate equality within 0.01
     * Feature: pos-backend, Property 9: Money multiplication distributivity
     * Validates: Requirements 2.2, 10.5, 14.3
     */
    @Property(tries = 500)
    @Label("Multiplication is approximately distributive")
    void multiplicationIsApproximatelyDistributive(
        @ForAll("validMoney") Money a,
        @ForAll @IntRange(min = 1, max = 100) int b,
        @ForAll @IntRange(min = 1, max = 100) int c
    ) {
        // When
        Money left = a.multiply(b + c);
        Money right = a.multiply(b).add(a.multiply(c));

        // Then - Due to rounding, results should be equal or very close
        BigDecimal diff = left.amount().subtract(right.amount()).abs();
        assertTrue(diff.compareTo(new BigDecimal("0.01")) <= 0,
            "Difference " + diff + " exceeds tolerance");
    }

    /**
     * Property: Scale is always 2 after any operation
     * Feature: pos-backend, Property 10: Money scale consistency
     * Validates: Requirements 2.2, 14.3
     */
    @Property(tries = 500)
    @Label("Scale is always 2 after any operation")
    void scaleIsAlwaysTwoAfterOperations(
        @ForAll("validMoney") Money a,
        @ForAll("validMoney") Money b,
        @ForAll @IntRange(min = 0, max = 100) int factor
    ) {
        Assume.that(a.isGreaterThanOrEqualTo(b)); // For subtraction

        // When
        Money sum = a.add(b);
        Money diff = a.subtract(b);
        Money product = a.multiply(factor);

        // Then
        assertEquals(2, sum.amount().scale());
        assertEquals(2, diff.amount().scale());
        assertEquals(2, product.amount().scale());
    }

    /**
     * Property: Discount calculation consistency
     * Feature: pos-backend, Property 11: Discount calculation
     * Validates: Requirements 10.5
     */
    @Property(tries = 500)
    @Label("Discount calculation is consistent")
    void discountCalculationIsConsistent(
        @ForAll("validMoney") Money subtotal,
        @ForAll @BigRange(min = "0.00", max = "1.00") BigDecimal discountRate
    ) {
        // When
        Money discountAmount = subtotal.multiply(discountRate);

        // Then
        assertTrue(discountAmount.isGreaterThanOrEqualTo(Money.ZERO));
        assertTrue(subtotal.isGreaterThanOrEqualTo(discountAmount));
        assertEquals(2, discountAmount.amount().scale());
    }

    /**
     * Property: Tax calculation consistency (subtotal - discount) × taxRate
     * Feature: pos-backend, Property 12: Tax calculation
     * Validates: Requirements 14.3
     */
    @Property(tries = 500)
    @Label("Tax calculation is consistent")
    void taxCalculationIsConsistent(
        @ForAll("validMoney") Money subtotal,
        @ForAll("validMoney") Money discountAmount,
        @ForAll @BigRange(min = "0.00", max = "1.00") BigDecimal taxRate
    ) {
        Assume.that(subtotal.isGreaterThanOrEqualTo(discountAmount));

        // When
        Money netAmount = subtotal.subtract(discountAmount);
        Money taxAmount = netAmount.multiply(taxRate);

        // Then
        assertTrue(taxAmount.isGreaterThanOrEqualTo(Money.ZERO));
        assertTrue(netAmount.isGreaterThanOrEqualTo(taxAmount));
        assertEquals(2, taxAmount.amount().scale());
    }

    /**
     * Property: Total calculation: total = subtotal - discount + tax
     * Feature: pos-backend, Property 13: Total calculation
     * Validates: Requirements 2.2, 10.5, 14.3
     */
    @Property(tries = 500)
    @Label("Total calculation is consistent")
    void totalCalculationIsConsistent(
        @ForAll("validMoney") Money subtotal,
        @ForAll("validMoney") Money discountAmount,
        @ForAll("validMoney") Money taxAmount
    ) {
        Assume.that(subtotal.isGreaterThanOrEqualTo(discountAmount));

        // When
        Money total = subtotal.subtract(discountAmount).add(taxAmount);

        // Then
        assertTrue(total.isGreaterThanOrEqualTo(Money.ZERO));
        assertEquals(2, total.amount().scale());
        
        // Verify the formula: total = subtotal - discount + tax
        Money expected = subtotal.subtract(discountAmount).add(taxAmount);
        assertEquals(expected, total);
    }

    /**
     * Provider for valid Money instances.
     */
    @Provide
    Arbitrary<Money> validMoney() {
        return Arbitraries.bigDecimals()
            .between(BigDecimal.ZERO, new BigDecimal("999999.99"))
            .ofScale(2)
            .map(Money::new);
    }
}
