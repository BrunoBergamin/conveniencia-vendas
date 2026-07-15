package com.conveniencia.vendas.infrastructure.catalogo;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** Cliente REST tipado do catalogo-service (config em application.properties). */
@RegisterRestClient(configKey = "catalogo")
@Path("/internal/estoque")
public interface CatalogoEstoqueClient {

    @POST
    @Path("/baixar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    EstoqueBaixaResponse baixar(@HeaderParam("Authorization") String autorizacao, EstoqueBaixaRequest req);
}
