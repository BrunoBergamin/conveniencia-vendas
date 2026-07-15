package com.conveniencia.catalogo.api.error;

import java.time.Instant;

/** Corpo padrao de erro da API. */
public record ErrorResponse(int status, String erro, String mensagem, Instant timestamp) {
    public static ErrorResponse de(int status, String erro, String mensagem) {
        return new ErrorResponse(status, erro, mensagem, Instant.now());
    }
}
