package br.com.fiap.fiapx.gateway.user.domain.shared;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("User already exists: " + email);
    }
}
