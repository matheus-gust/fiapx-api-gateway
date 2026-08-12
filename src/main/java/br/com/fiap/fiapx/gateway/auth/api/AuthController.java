package br.com.fiap.fiapx.gateway.auth.api;

import br.com.fiap.fiapx.gateway.auth.application.AuthAppService;
import br.com.fiap.fiapx.gateway.auth.application.dtos.AuthResponseDTO;
import br.com.fiap.fiapx.gateway.auth.application.dtos.LoginRequestDTO;
import br.com.fiap.fiapx.gateway.auth.application.dtos.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Registro e login de usuarios")
public class AuthController {

    private final AuthAppService authAppService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar novo usuario")
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto) {
        return authAppService.register(dto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login e obtencao de JWT")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return authAppService.login(dto);
    }
}
