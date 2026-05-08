package com.pos.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SKU value object.
 * Validates: Requirements 1.2, 6.1
 */
class SKUTest {

    @Test
    void shouldCreateValidSKU() {
        // Given
        String validValue = "PROD-123";
        
        // When
        SKU sku = new SKU(validValue);
        
        // Then
        assertNotNull(sku);
        assertEquals(validValue, sku.value());
        assertEquals(validValue, sku.toString());
    }

    @Test
    void shouldAcceptAlphanumericWithHyphens() {
        // Valid SKU patterns
        assertDoesNotThrow(() -> new SKU("ABC123"));
        assertDoesNotThrow(() -> new SKU("PROD-001"));
        assertDoesNotThrow(() -> new SKU("SKU-ABC-123"));
        assertDoesNotThrow(() -> new SKU("a1b2c3"));
        assertDoesNotThrow(() -> new SKU("A"));
        assertDoesNotThrow(() -> new SKU("1"));
        assertDoesNotThrow(() -> new SKU("-"));
    }

    @Test
    void shouldAcceptMaxLength50() {
        // Exactly 50 characters
        String fiftyChars = "12345678901234567890123456789012345678901234567890";
        assertDoesNotThrow(() -> new SKU(fiftyChars));
        assertEquals(50, fiftyChars.length());
    }

    @Test
    void shouldRejectNullValue() {
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new SKU(null)
        );
        assertTrue(exception.getMessage().contains("SKU value must not be null"));
    }

    @Test
    void shouldRejectBlankValue() {
        // Empty string
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> new SKU("")
        );
        assertTrue(exception1.getMessage().contains("SKU value must not be blank"));

        // Whitespace only
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> new SKU("   ")
        );
        assertTrue(exception2.getMessage().contains("SKU value must not be blank"));
    }

    @Test
    void shouldRejectValueExceeding50Characters() {
        // 51 characters
        String fiftyOneChars = "123456789012345678901234567890123456789012345678901";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SKU(fiftyOneChars)
        );
        assertTrue(exception.getMessage().contains("must not exceed 50 characters"));
        assertEquals(51, fiftyOneChars.length());
    }

    @Test
    void shouldRejectNonAlphanumericCharacters() {
        // Special characters not allowed
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD@123"));
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD#123"));
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD$123"));
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD%123"));
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD&123"));
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD*123"));
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD 123")); // space
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD_123")); // underscore
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD.123")); // dot
        assertThrows(IllegalArgumentException.class, () -> new SKU("PROD/123")); // slash
    }

    @Test
    void shouldSupportEquality() {
        // Given
        SKU sku1 = new SKU("PROD-123");
        SKU sku2 = new SKU("PROD-123");
        SKU sku3 = new SKU("PROD-456");
        
        // Then
        assertEquals(sku1, sku2);
        assertNotEquals(sku1, sku3);
        assertEquals(sku1.hashCode(), sku2.hashCode());
    }

    @Test
    void shouldSupportToString() {
        // Given
        String value = "PROD-123";
        SKU sku = new SKU(value);
        
        // Then
        assertEquals(value, sku.toString());
    }
}
