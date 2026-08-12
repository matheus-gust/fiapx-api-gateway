package br.com.fiap.fiapx.user.application;

import br.com.fiap.fiapx.user.application.dtos.AuthResponseDTO;
import br.com.fiap.fiapx.user.application.dtos.LoginRequestDTO;
import br.com.fiap.fiapx.user.application.dtos.RegisterRequestDTO;
import br.com.fiap.fiapx.user.domain.model.User;
import br.com.fiap.fiapx.user.domain.repository.UserRepository;
import br.com.fiap.fiapx.user.infra.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        User saved = userRepository.save(user);
        UserDetails details = userDetailsService.loadUserByUsername(saved.getEmail());
        String token = jwtService.generateToken(details);
        return new AuthResponseDTO(token, saved.getEmail(), saved.getName());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails details = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(details);
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return new AuthResponseDTO(token, user.getEmail(), user.getName());
    }
}
