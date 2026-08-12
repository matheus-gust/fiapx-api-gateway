package br.com.fiap.fiapx.bdd;

import br.com.fiap.fiapx.user.application.AuthService;
import br.com.fiap.fiapx.user.application.dtos.AuthResponseDTO;
import br.com.fiap.fiapx.user.application.dtos.LoginRequestDTO;
import br.com.fiap.fiapx.user.application.dtos.RegisterRequestDTO;
import br.com.fiap.fiapx.user.domain.model.User;
import br.com.fiap.fiapx.user.domain.repository.UserRepository;
import br.com.fiap.fiapx.user.infra.security.JwtService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthSteps {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService = Mockito.mock(JwtService.class);
    private final AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
    private final UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
    private final AuthService authService = new AuthService(
            userRepository, passwordEncoder, jwtService, authManager, userDetailsService);

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private AuthResponseDTO response;
    private Exception thrownException;

    @Given("que o usuario {string} com email {string} e senha {string} nao existe")
    public void usuarioNaoExiste(String name, String email, String password) {
        registerRequest = new RegisterRequestDTO(name, email, password);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        UserDetails ud = org.springframework.security.core.userdetails.User
                .withUsername(email).password("encoded").roles("USER").build();
        User savedUser = User.builder().id(1L).name(name).email(email).password("encoded").build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(ud);
        when(jwtService.generateToken(ud)).thenReturn("jwt-token");
    }

    @Given("que o usuario {string} com email {string} ja esta cadastrado com senha {string}")
    public void usuarioCadastrado(String name, String email, String password) {
        loginRequest = new LoginRequestDTO(email, password);
        UserDetails ud = org.springframework.security.core.userdetails.User
                .withUsername(email).password(passwordEncoder.encode(password)).roles("USER").build();
        User savedUser = User.builder().id(1L).name(name).email(email).password("encoded").build();
        when(userDetailsService.loadUserByUsername(email)).thenReturn(ud);
        when(jwtService.generateToken(ud)).thenReturn("jwt-token");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(savedUser));
    }

    @Given("que o usuario com email {string} ja esta cadastrado")
    public void usuarioJaCadastrado(String email) {
        registerRequest = new RegisterRequestDTO("Dup", email, "senha123");
        when(userRepository.existsByEmail(email)).thenReturn(true);
    }

    @When("o usuario realiza o registro com esses dados")
    public void realizaRegistro() {
        try {
            response = authService.register(registerRequest);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("o usuario realiza o login com email {string} e senha {string}")
    public void realizaLogin(String email, String password) {
        loginRequest = new LoginRequestDTO(email, password);
        response = authService.login(loginRequest);
    }

    @When("o usuario tenta se registrar com o mesmo email {string}")
    public void tentaRegistrarEmailDuplicado(String email) {
        try {
            authService.register(registerRequest);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("o sistema retorna um token JWT valido")
    public void retornaTokenValido() {
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt-token");
    }

    @Then("o sistema retorna um erro de conflito")
    public void retornaErroConflito() {
        assertThat(thrownException).isInstanceOf(IllegalArgumentException.class);
    }
}
