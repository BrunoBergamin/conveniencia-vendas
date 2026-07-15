package com.conveniencia.vendas.api.error;

import com.conveniencia.vendas.application.venda.CatalogoIndisponivelException;
import com.conveniencia.vendas.application.venda.SemEstoqueException;
import com.conveniencia.vendas.domain.caixa.CaixaJaAbertoException;
import com.conveniencia.vendas.domain.caixa.CaixaJaFechadoException;
import com.conveniencia.vendas.domain.caixa.CaixaNaoAbertoException;
import com.conveniencia.vendas.domain.shared.DomainException;
import com.conveniencia.vendas.domain.shared.EntidadeNaoEncontradaException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Traduz as excecoes de negocio em respostas HTTP consistentes. */
@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    @Override
    public Response toResponse(DomainException ex) {
        Response.Status status;
        String codigo;
        if (ex instanceof EntidadeNaoEncontradaException) {
            status = Response.Status.NOT_FOUND;
            codigo = "NAO_ENCONTRADO";
        } else if (ex instanceof CatalogoIndisponivelException) {
            status = Response.Status.SERVICE_UNAVAILABLE;
            codigo = "CATALOGO_INDISPONIVEL";
        } else if (ex instanceof SemEstoqueException) {
            status = Response.Status.CONFLICT;
            codigo = "SEM_ESTOQUE";
        } else if (ex instanceof CaixaNaoAbertoException) {
            status = Response.Status.CONFLICT;
            codigo = "CAIXA_NAO_ABERTO";
        } else if (ex instanceof CaixaJaAbertoException) {
            status = Response.Status.CONFLICT;
            codigo = "CAIXA_JA_ABERTO";
        } else if (ex instanceof CaixaJaFechadoException) {
            status = Response.Status.CONFLICT;
            codigo = "CAIXA_JA_FECHADO";
        } else {
            status = Response.Status.BAD_REQUEST;
            codigo = "REGRA_NEGOCIO";
        }
        return Response.status(status)
                .entity(ErrorResponse.de(status.getStatusCode(), codigo, ex.getMessage()))
                .build();
    }
}
