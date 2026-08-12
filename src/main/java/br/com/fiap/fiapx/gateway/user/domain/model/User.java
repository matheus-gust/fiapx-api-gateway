package br.com.fiap.fiapx.gateway.user.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String name,
        String email,
        String password,
        LocalDateTime createdAt
) {}
