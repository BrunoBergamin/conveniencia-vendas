package com.conveniencia.vendas.infrastructure.security;

import com.conveniencia.vendas.api.error.ErrorResponse;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Exige um JWT valido em toda rota da aplicacao. As rotas de gestao do Quarkus
 * (/q/health, /q/metrics, /q/openapi, /q/swagger-ui) nao passam por aqui.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AutenticacaoFilter implements ContainerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    @Inject
    JwtValidador validador;
    @Inject
    UsuarioAutenticado usuario;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String header = ctx.getHeaderString("Authorization");
        if (header == null || !header.startsWith(PREFIXO)) {
            recusar(ctx);
            return;
        }
        String token = header.substring(PREFIXO.length());
        var principal = validador.validar(token);
        if (principal.isEmpty()) {
            recusar(ctx);
            return;
        }
        usuario.preencher(principal.get().login(), principal.get().papel(), token);
    }

    private void recusar(ContainerRequestContext ctx) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(ErrorResponse.de(401, "NAO_AUTORIZADO", "token ausente ou invalido"))
                .build());
    }
}
