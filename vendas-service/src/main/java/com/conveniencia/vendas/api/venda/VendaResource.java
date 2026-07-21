package com.conveniencia.vendas.api.venda;

import com.conveniencia.vendas.application.venda.ItemRequisitado;
import com.conveniencia.vendas.application.venda.VendaApplicationService;
import com.conveniencia.vendas.domain.venda.Venda;
import com.conveniencia.vendas.infrastructure.security.UsuarioAutenticado;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/vendas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Vendas")
public class VendaResource {

    @Inject
    VendaApplicationService vendas;
    @Inject
    UsuarioAutenticado usuario;

    @POST
    @Operation(summary = "Registra uma venda no caixa aberto do operador (idempotente pela chave)")
    public Response registrar(@Valid RegistrarVendaRequest req) {
        List<ItemRequisitado> itens = req.itens().stream()
                .map(i -> new ItemRequisitado(i.produtoId(), i.quantidade()))
                .toList();
        Venda venda = vendas.registrar(usuario.login(), req.chaveIdempotencia(), itens,
                req.formaPagamento(), usuario.autorizacao());
        return Response.status(Response.Status.CREATED).entity(VendaResponse.de(venda)).build();
    }
}
