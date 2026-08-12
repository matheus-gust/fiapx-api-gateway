package br.com.fiap.fiapx.user.infra.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "test-secret-key-for-testing-only-must-be-256-bits!!");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        UserDetails userDetails = buildUserDetails("user@test.com");
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnCorrectEmail() {
        UserDetails userDetails = buildUserDetails("user@test.com");
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.extractUsername(token)).isEqualTo("user@test.com");
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        UserDetails userDetails = buildUserDetails("user@test.com");
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForWrongUser() {
        UserDetails userA = buildUserDetails("a@test.com");
        UserDetails userB = buildUserDetails("b@test.com");
        String token = jwtService.generateToken(userA);
        assertThat(jwtService.isTokenValid(token, userB)).isFalse();
    }

    private UserDetails buildUserDetails(String email) {
        return User.withUsername(email).password("pass").roles("USER").build();
    }
}
