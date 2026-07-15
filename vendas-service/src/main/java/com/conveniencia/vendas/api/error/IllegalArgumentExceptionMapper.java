package com.conveniencia.vendas.api.error;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Argumento invalido do dominio (ex.: quantidade nao positiva) vira 400. */
@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ErrorResponse.de(400, "REQUISICAO_INVALIDA", ex.getMessage()))
                .build();
    }
}
