package com.conveniencia.vendas.infrastructure.catalogo;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.UUID;

/** Cliente REST tipado do catalogo-service (config em application.properties). */
@RegisterRestClient(configKey = "catalogo")
@Path("/internal/estoque")
public interface CatalogoEstoqueClient {

    @POST
    @Path("/baixar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    EstoqueBaixaResponse baixar(@HeaderParam("Authorization") String autorizacao,
                                @HeaderParam("Idempotency-Key") String chave,
                                EstoqueBaixaRequest req);

    @POST
    @Path("/estornar")
    @Produces(MediaType.APPLICATION_JSON)
    EstornoResponse estornar(@HeaderParam("Authorization") String autorizacao,
                             @HeaderParam("Idempotency-Key") String chave);

    @GET
    @Path("/operacoes-efetivadas")
    @Produces(MediaType.APPLICATION_JSON)
    OperacoesEfetivadasResponse operacoesEfetivadas(@HeaderParam("Authorization") String autorizacao,
                                                    @QueryParam("antesDe") String antesDe);

    record EstornoResponse(boolean estornada) {
    }

    record OperacoesEfetivadasResponse(List<UUID> chaves) {
    }
}
