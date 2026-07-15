package com.conveniencia.catalogo.application.identidade;

/** Token emitido e por quanto tempo (segundos) ele vale. */
public record TokenEmitido(String token, long expiraEmSegundos) {
}
