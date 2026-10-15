package ma.enset.jwt_security_tp.dto;

public record RegisterRequest(
        String email,
        String password
) {}
