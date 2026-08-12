package br.com.fiap.fiapx.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
}
