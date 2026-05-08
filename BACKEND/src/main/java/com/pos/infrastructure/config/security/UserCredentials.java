package com.pos.infrastructure.config.security;

/**
 * Security value object holding user authentication data.
 * Not a domain entity — belongs to the security infrastructure context.
 */
public record UserCredentials(String username, String passwordHash, String role) {
}
