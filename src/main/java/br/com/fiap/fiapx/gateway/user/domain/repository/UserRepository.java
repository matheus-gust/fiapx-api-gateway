package br.com.fiap.fiapx.gateway.user.domain.repository;

import br.com.fiap.fiapx.gateway.user.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    boolean existsByEmail(String email);
}
