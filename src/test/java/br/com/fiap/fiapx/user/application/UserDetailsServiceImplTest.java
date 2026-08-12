package br.com.fiap.fiapx.user.application;

import br.com.fiap.fiapx.user.domain.model.User;
import br.com.fiap.fiapx.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = User.builder().id(1L).name("Maria").email("maria@test.com").password("encoded").build();
        when(userRepository.findByEmail("maria@test.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("maria@test.com");

        assertThat(details.getUsername()).isEqualTo("maria@test.com");
        assertThat(details.getPassword()).isEqualTo("encoded");
    }

    @Test
    void loadUserByUsername_shouldThrowWhenNotFound() {
        when(userRepository.findByEmail("naoexiste@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("naoexiste@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
