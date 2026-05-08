package com.pos.infrastructure.adapter.output.persistence;

import com.pos.application.port.output.UserRepository;
import com.pos.infrastructure.config.security.UserCredentials;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

@Component
@Profile("!mock")
public class UserJpaAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserJpaAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UserCredentials> findByUsername(String username) {
        return jpaRepository.findByUsernameAndActiveTrue(username)
            .map(e -> new UserCredentials(e.getUsername(), e.getPasswordHash(), e.getRole()));
    }
}
