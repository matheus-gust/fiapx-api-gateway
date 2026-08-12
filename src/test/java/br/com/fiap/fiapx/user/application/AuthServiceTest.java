package br.com.fiap.fiapx.user.application;

import br.com.fiap.fiapx.user.application.dtos.AuthResponseDTO;
import br.com.fiap.fiapx.user.application.dtos.LoginRequestDTO;
import br.com.fiap.fiapx.user.application.dtos.RegisterRequestDTO;
import br.com.fiap.fiapx.user.domain.model.User;
import br.com.fiap.fiapx.user.domain.repository.UserRepository;
import br.com.fiap.fiapx.user.infra.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Joao").email("joao@test.com").password("encoded").build();
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("joao@test.com").password("encoded").roles("USER").build();
    }

    @Test
    void register_shouldCreateUserAndReturnToken() {
        RegisterRequestDTO request = new RegisterRequestDTO("Joao", "joao@test.com", "senha123");
        when(userRepository.existsByEmail("joao@test.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);
        when(userDetailsService.loadUserByUsername("joao@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token-jwt");

        AuthResponseDTO response = authService.register(request);

        assertThat(response.token()).isEqualTo("token-jwt");
        assertThat(response.email()).isEqualTo("joao@test.com");
        assertThat(response.name()).isEqualTo("Joao");
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        RegisterRequestDTO request = new RegisterRequestDTO("Joao", "joao@test.com", "senha123");
        when(userRepository.existsByEmail("joao@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail já cadastrado");
    }

    @Test
    void login_shouldAuthenticateAndReturnToken() {
        LoginRequestDTO request = new LoginRequestDTO("joao@test.com", "senha123");
        when(userDetailsService.loadUserByUsername("joao@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token-jwt");
        when(userRepository.findByEmail("joao@test.com")).thenReturn(Optional.of(user));

        AuthResponseDTO response = authService.login(request);

        assertThat(response.token()).isEqualTo("token-jwt");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void register_shouldEncodePassword() {
        RegisterRequestDTO request = new RegisterRequestDTO("Joao", "joao@test.com", "senha123");
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(user);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("token");

        authService.register(request);

        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(argThat(u -> u.getPassword().equals("hashed")));
    }
}
