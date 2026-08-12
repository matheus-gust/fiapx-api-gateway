package br.com.fiap.fiapx.user.infra.persistence.repository;

import br.com.fiap.fiapx.user.domain.model.User;
import br.com.fiap.fiapx.user.domain.repository.UserRepository;
import br.com.fiap.fiapx.user.infra.persistence.entity.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        UserJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private User toDomain(UserJpaEntity e) {
        return User.builder().id(e.getId()).name(e.getName()).email(e.getEmail()).password(e.getPassword()).build();
    }
}
