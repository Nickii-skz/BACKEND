package com.pos.infrastructure.adapter.input.dto.response;

/**
 * DTO response for authentication token.
 */
public record TokenResponse(
    String token,
    String tokenType,
    long expiresIn
) {
    public static TokenResponse of(String token, long expiresIn) {
        return new TokenResponse(token, "Bearer", expiresIn);
    }
}
