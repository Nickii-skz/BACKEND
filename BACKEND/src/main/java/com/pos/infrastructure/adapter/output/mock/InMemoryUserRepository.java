package com.pos.infrastructure.adapter.output.mock;

import com.pos.application.port.output.UserRepository;
import com.pos.infrastructure.config.security.UserCredentials;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("mock")
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, UserCredentials> storage = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        // Usuarios de ejemplo con contraseñas en texto plano (solo para mock)
        storage.put("admin", new UserCredentials(
            "admin",
            "admin123",
            "ROLE_ADMIN"
        ));
        
        storage.put("cashier", new UserCredentials(
            "cashier",
            "cashier123",
            "ROLE_CASHIER"
        ));
    }

    @Override
    public Optional<UserCredentials> findByUsername(String username) {
        return Optional.ofNullable(storage.get(username));
    }
}
