package com.conveniencia.vendas.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/** Valida o JWT HS256 emitido pelo catalogo-service (mesmo segredo). */
@ApplicationScoped
public class JwtValidador {

    private final SecretKey key;

    public JwtValidador(@ConfigProperty(name = "jwt.secret") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<Principal> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new Principal(claims.getSubject(), claims.get("papel", String.class)));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    public record Principal(String login, String papel) {
    }
}
