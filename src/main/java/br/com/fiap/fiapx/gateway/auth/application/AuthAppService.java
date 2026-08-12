package br.com.fiap.fiapx.gateway.auth.application;

import br.com.fiap.fiapx.gateway.auth.application.dtos.AuthResponseDTO;
import br.com.fiap.fiapx.gateway.auth.application.dtos.LoginRequestDTO;
import br.com.fiap.fiapx.gateway.auth.application.dtos.RegisterRequestDTO;
import br.com.fiap.fiapx.gateway.security.JwtTokenProvider;
import br.com.fiap.fiapx.gateway.user.domain.model.User;
import br.com.fiap.fiapx.gateway.user.domain.repository.UserRepository;
import br.com.fiap.fiapx.gateway.user.domain.shared.UserAlreadyExistsException;
import br.com.fiap.fiapx.gateway.user.domain.shared.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthAppService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException(dto.email());
        }
        User user = new User(UUID.randomUUID(), dto.name(), dto.email(),
                passwordEncoder.encode(dto.password()), LocalDateTime.now());
        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(saved.id(), saved.email());
        return new AuthResponseDTO(token, saved.email(), saved.name());
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UserNotFoundException(dto.email()));
        if (!passwordEncoder.matches(dto.password(), user.password())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtTokenProvider.generateToken(user.id(), user.email());
        return new AuthResponseDTO(token, user.email(), user.name());
    }
}
