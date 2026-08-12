package br.com.fiap.fiapx.user.api;

import br.com.fiap.fiapx.user.application.AuthService;
import br.com.fiap.fiapx.user.application.dtos.AuthResponseDTO;
import br.com.fiap.fiapx.user.application.dtos.LoginRequestDTO;
import br.com.fiap.fiapx.user.application.dtos.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registro e login de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra novo usuário")
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica usuário e retorna JWT")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }
}
