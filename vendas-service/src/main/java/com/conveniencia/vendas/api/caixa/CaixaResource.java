package com.conveniencia.vendas.api.caixa;

import com.conveniencia.vendas.application.caixa.CaixaApplicationService;
import com.conveniencia.vendas.domain.caixa.Caixa;
import com.conveniencia.vendas.domain.venda.Dinheiro;
import com.conveniencia.vendas.infrastructure.security.UsuarioAutenticado;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/caixas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Caixa")
public class CaixaResource {

    @Inject
    CaixaApplicationService caixas;
    @Inject
    UsuarioAutenticado usuario;

    @POST
    @Operation(summary = "Abre um caixa (turno) para o operador logado")
    public Response abrir(@Valid AbrirCaixaRequest req) {
        Caixa caixa = caixas.abrir(usuario.login(), new Dinheiro(req.fundoTroco()));
        return Response.status(Response.Status.CREATED).entity(CaixaResponse.de(caixa)).build();
    }

    @POST
    @Path("/{id}/fechar")
    @Operation(summary = "Fecha um caixa")
    public CaixaResponse fechar(@PathParam("id") UUID id) {
        return CaixaResponse.de(caixas.fechar(id));
    }

    @GET
    @Path("/aberto")
    @Operation(summary = "Retorna o caixa aberto do operador logado (204 se nao houver)")
    public Response aberto() {
        return caixas.consultarAberto(usuario.login())
                .map(c -> Response.ok(CaixaResponse.de(c)).build())
                .orElseGet(() -> Response.noContent().build());
    }
}
