package com.pos;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuración de datos mock para ejecutar sin base de datos
 */
@Configuration
@Profile("mock")
public class MockDataConfig {

    // Datos en memoria
    private final Map<String, MockProduct> products = new ConcurrentHashMap<>();
    private final Map<UUID, MockCategory> categories = new ConcurrentHashMap<>();
    private final Map<UUID, MockSale> sales = new ConcurrentHashMap<>();

    public MockDataConfig() {
        initializeMockData();
    }

    private void initializeMockData() {
        // Crear categorías
        UUID cat1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UUID cat2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        UUID cat3 = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");

        categories.put(cat1, new MockCategory(cat1, "Electrónica", "Productos electrónicos y tecnología", true));
        categories.put(cat2, new MockCategory(cat2, "Ropa", "Ropa y accesorios", true));
        categories.put(cat3, new MockCategory(cat3, "Hogar", "Artículos para el hogar", true));

        // Crear productos
        products.put("LAPTOP-001", new MockProduct("LAPTOP-001", "Laptop Dell XPS 15", 
            "Laptop de alto rendimiento con 16GB RAM", "https://example.com/laptop.jpg",
            new BigDecimal("1299.99"), 50, true, cat1));
        
        products.put("MOUSE-001", new MockProduct("MOUSE-001", "Mouse Logitech MX Master 3",
            "Mouse inalámbrico ergonómico", "https://example.com/mouse.jpg",
            new BigDecimal("99.99"), 100, true, cat1));
        
        products.put("TECLADO-001", new MockProduct("TECLADO-001", "Teclado Mecánico Corsair",
            "Teclado mecánico RGB", "https://example.com/keyboard.jpg",
            new BigDecimal("149.99"), 75, true, cat1));
        
        products.put("CAMISA-001", new MockProduct("CAMISA-001", "Camisa Formal Azul",
            "Camisa de vestir 100% algodón", "https://example.com/shirt.jpg",
            new BigDecimal("49.99"), 200, true, cat2));
        
        products.put("LAMPARA-001", new MockProduct("LAMPARA-001", "Lámpara LED Escritorio",
            "Lámpara LED ajustable", "https://example.com/lamp.jpg",
            new BigDecimal("29.99"), 150, true, cat3));
    }

    @Bean
    public Map<String, MockProduct> mockProducts() {
        return products;
    }

    @Bean
    public Map<UUID, MockCategory> mockCategories() {
        return categories;
    }

    @Bean
    public Map<UUID, MockSale> mockSales() {
        return sales;
    }

    // Clases de datos mock
    public static class MockProduct {
        public String sku;
        public String name;
        public String description;
        public String imageUrl;
        public BigDecimal unitPrice;
        public int stockQuantity;
        public boolean active;
        public UUID categoryId;
        public LocalDateTime createdAt;

        public MockProduct(String sku, String name, String description, String imageUrl,
                          BigDecimal unitPrice, int stockQuantity, boolean active, UUID categoryId) {
            this.sku = sku;
            this.name = name;
            this.description = description;
            this.imageUrl = imageUrl;
            this.unitPrice = unitPrice;
            this.stockQuantity = stockQuantity;
            this.active = active;
            this.categoryId = categoryId;
            this.createdAt = LocalDateTime.now();
        }
    }

    public static class MockCategory {
        public UUID id;
        public String name;
        public String description;
        public boolean active;
        public LocalDateTime createdAt;

        public MockCategory(UUID id, String name, String description, boolean active) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.active = active;
            this.createdAt = LocalDateTime.now();
        }
    }

    public static class MockSale {
        public UUID id;
        public BigDecimal subtotal;
        public BigDecimal discountAmount;
        public BigDecimal taxAmount;
        public BigDecimal total;
        public String paymentMethod;
        public String status;
        public LocalDateTime createdAt;

        public MockSale(UUID id, BigDecimal subtotal, BigDecimal discountAmount,
                       BigDecimal taxAmount, BigDecimal total, String paymentMethod) {
            this.id = id;
            this.subtotal = subtotal;
            this.discountAmount = discountAmount;
            this.taxAmount = taxAmount;
            this.total = total;
            this.paymentMethod = paymentMethod;
            this.status = "PENDING";
            this.createdAt = LocalDateTime.now();
        }
    }
}
