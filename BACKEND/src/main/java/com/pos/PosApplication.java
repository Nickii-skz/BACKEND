package com.pos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * POS Backend — Main Application Entry Point.
 *
 * <p>Spring Boot 3.x application following Hexagonal Architecture (Ports &amp; Adapters).
 * The domain and application layers are completely independent of Spring, JPA, and any
 * infrastructure concern.
 */
@SpringBootApplication
public class PosApplication {

    public static void main(String[] args) {
        SpringApplication.run(PosApplication.class, args);
    }
}
