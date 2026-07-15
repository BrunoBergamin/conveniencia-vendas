package com.conveniencia.catalogo.api.auth;

public record LoginResponse(String token, long expiraEmSegundos) {
}
