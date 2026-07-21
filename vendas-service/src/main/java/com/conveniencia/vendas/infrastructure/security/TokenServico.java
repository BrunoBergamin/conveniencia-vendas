package com.conveniencia.vendas.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Emite o token usado em chamadas que nao nascem de um request de operador
 * (ex.: job de reconciliacao). Assina com o mesmo segredo HS256 compartilhado
 * dos servicos, entao o catalogo valida sem saber que existe "servico":
 * e um principal como outro qualquer, com papel proprio para auditoria.
 */
@ApplicationScoped
public class TokenServico {

    private static final Duration VALIDADE = Duration.ofMinutes(5);

    private final SecretKey key;

    public TokenServico(@ConfigProperty(name = "jwt.secret") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Devolve o valor pronto do header Authorization ("Bearer ..."). */
    public String bearer() {
        Instant agora = Instant.now();
        String token = Jwts.builder()
                .subject("vendas-service")
                .claim("papel", "SERVICO")
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(VALIDADE)))
                .signWith(key)
                .compact();
        return "Bearer " + token;
    }
}
