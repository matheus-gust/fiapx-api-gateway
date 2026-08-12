package br.com.fiap.fiapx.gateway.user.domain.shared;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("User not found: " + email);
    }
}
