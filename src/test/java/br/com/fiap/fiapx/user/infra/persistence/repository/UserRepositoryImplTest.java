package br.com.fiap.fiapx.user.infra.persistence.repository;

import br.com.fiap.fiapx.user.domain.model.User;
import br.com.fiap.fiapx.user.infra.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    UserJpaRepository jpaRepository;

    UserRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepositoryImpl(jpaRepository);
    }

    @Test
    void save_shouldPersistAndReturnDomainUser() {
        User user = User.builder().name("Joao").email("joao@test.com").password("hash").build();
        UserJpaEntity saved = UserJpaEntity.builder()
                .id(1L).name("Joao").email("joao@test.com").password("hash").build();
        when(jpaRepository.save(any())).thenReturn(saved);

        User result = repository.save(user);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("joao@test.com");
    }

    @Test
    void findByEmail_shouldReturnUserWhenFound() {
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(2L).name("Maria").email("maria@test.com").password("hash").build();
        when(jpaRepository.findByEmail("maria@test.com")).thenReturn(Optional.of(entity));

        Optional<User> result = repository.findByEmail("maria@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Maria");
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        Optional<User> result = repository.findByEmail("ghost@test.com");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_shouldDelegateToJpaRepository() {
        when(jpaRepository.existsByEmail("joao@test.com")).thenReturn(true);

        assertThat(repository.existsByEmail("joao@test.com")).isTrue();
    }
}
