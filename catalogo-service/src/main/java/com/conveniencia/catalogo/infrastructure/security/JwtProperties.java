package com.conveniencia.catalogo.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Config do JWT (prefixo "jwt" no application.yml).
 * O segredo vem de variavel de ambiente / Secret do Kubernetes, nunca do codigo.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secret, long expiracaoSegundos) {
}
