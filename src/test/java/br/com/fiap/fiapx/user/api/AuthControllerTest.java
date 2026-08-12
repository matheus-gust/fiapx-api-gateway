package br.com.fiap.fiapx.user.api;

import br.com.fiap.fiapx.user.application.AuthService;
import br.com.fiap.fiapx.user.application.dtos.AuthResponseDTO;
import br.com.fiap.fiapx.user.application.dtos.LoginRequestDTO;
import br.com.fiap.fiapx.user.application.dtos.RegisterRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(br.com.fiap.fiapx.config.SecurityConfig.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthService authService;
    @MockBean br.com.fiap.fiapx.user.infra.security.JwtAuthFilter jwtAuthFilter;
    @MockBean org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockBean br.com.fiap.fiapx.user.infra.security.JwtService jwtService;
    @MockBean org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @MockBean org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Test
    void register_shouldReturn201WithToken() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("Joao", "joao@test.com", "senha123");
        AuthResponseDTO response = new AuthResponseDTO("token-abc", "joao@test.com", "Joao");
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-abc"))
                .andExpect(jsonPath("$.email").value("joao@test.com"));
    }

    @Test
    void login_shouldReturn200WithToken() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("joao@test.com", "senha123");
        AuthResponseDTO response = new AuthResponseDTO("token-abc", "joao@test.com", "Joao");
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-abc"));
    }

    @Test
    void register_shouldReturn400WhenEmailInvalid() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("Joao", "email-invalido", "senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn401WhenBadCredentials() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("joao@test.com", "errada");
        when(authService.login(any())).thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
