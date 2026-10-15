package ma.enset.jwt_security_tp.dto;

public record LoginRequest(
        String email,
        String password
) {}
