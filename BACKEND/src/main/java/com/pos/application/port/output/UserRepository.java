package com.pos.application.port.output;

import com.pos.infrastructure.config.security.UserCredentials;

import java.util.Optional;

/**
 * Output port: contract for user credential lookup.
 * Belongs to the security context, not the business domain.
 * Implemented by the infrastructure layer.
 */
public interface UserRepository {

    Optional<UserCredentials> findByUsername(String username);
}
