package com.conveniencia.catalogo.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/** Gera e valida o JWT HS256. Mesmo segredo usado pelos dois servicos. */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expiracaoSegundos;

    public JwtService(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.expiracaoSegundos = props.expiracaoSegundos();
    }

    public String gerarToken(String login, String papel) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(login)
                .claim("papel", papel)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusSeconds(expiracaoSegundos)))
                .signWith(key)
                .compact();
    }

    public long expiracaoSegundos() {
        return expiracaoSegundos;
    }

    /** Valida a assinatura e a validade. Vazio se o token for invalido. */
    public Optional<PrincipalAutenticado> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new PrincipalAutenticado(claims.getSubject(), claims.get("papel", String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public record PrincipalAutenticado(String login, String papel) {
    }
}
