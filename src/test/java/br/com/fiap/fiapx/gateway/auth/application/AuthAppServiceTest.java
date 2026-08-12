package br.com.fiap.fiapx.gateway.auth.application;

import br.com.fiap.fiapx.gateway.auth.application.dtos.AuthResponseDTO;
import br.com.fiap.fiapx.gateway.auth.application.dtos.LoginRequestDTO;
import br.com.fiap.fiapx.gateway.auth.application.dtos.RegisterRequestDTO;
import br.com.fiap.fiapx.gateway.config.JwtConfig;
import br.com.fiap.fiapx.gateway.security.JwtTokenProvider;
import br.com.fiap.fiapx.gateway.user.domain.model.User;
import br.com.fiap.fiapx.gateway.user.domain.repository.UserRepository;
import br.com.fiap.fiapx.gateway.user.domain.shared.UserAlreadyExistsException;
import br.com.fiap.fiapx.gateway.user.domain.shared.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthAppServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthAppService authAppService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User(UUID.randomUUID(), "Test User", "test@email.com",
                "$2a$encoded", LocalDateTime.now());
    }

    @Test
    void register_success() {
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("$2a$encoded");
        when(userRepository.save(any())).thenReturn(
                new User(UUID.randomUUID(), "New User", "new@email.com", "$2a$encoded", LocalDateTime.now()));
        when(jwtTokenProvider.generateToken(any(), any())).thenReturn("jwt-token");

        AuthResponseDTO result = authAppService.register(new RegisterRequestDTO("New User", "new@email.com", "password"));

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.email()).isEqualTo("new@email.com");
    }

    @Test
    void register_throwsWhenEmailExists() {
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authAppService.register(
                new RegisterRequestDTO("Test", "test@email.com", "password")))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void login_success() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password", "$2a$encoded")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), any())).thenReturn("jwt-token");

        AuthResponseDTO result = authAppService.login(new LoginRequestDTO("test@email.com", "password"));

        assertThat(result.token()).isEqualTo("jwt-token");
    }

    @Test
    void login_throwsWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authAppService.login(new LoginRequestDTO("unknown@email.com", "pass")))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void login_throwsWhenBadPassword() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong", "$2a$encoded")).thenReturn(false);

        assertThatThrownBy(() -> authAppService.login(new LoginRequestDTO("test@email.com", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }
}
