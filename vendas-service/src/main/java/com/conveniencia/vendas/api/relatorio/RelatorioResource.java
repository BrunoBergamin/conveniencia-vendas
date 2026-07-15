package com.conveniencia.vendas.api.relatorio;

import com.conveniencia.vendas.application.relatorio.RelatorioApplicationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Path("/relatorios")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Relatorios")
public class RelatorioResource {

    @Inject
    RelatorioApplicationService relatorios;

    @GET
    @Path("/dia")
    @Operation(summary = "Resumo das vendas de um dia (padrao: hoje)")
    public ResumoVendasResponse doDia(@QueryParam("data") String data) {
        LocalDate dia = (data == null || data.isBlank())
                ? LocalDate.now(ZoneOffset.UTC)
                : LocalDate.parse(data);
        return ResumoVendasResponse.de(relatorios.doDia(dia));
    }

    @GET
    @Path("/caixa/{id}")
    @Operation(summary = "Resumo das vendas de um caixa")
    public ResumoVendasResponse doCaixa(@PathParam("id") UUID id) {
        return ResumoVendasResponse.de(relatorios.doCaixa(id));
    }
}
