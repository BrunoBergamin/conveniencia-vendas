package com.conveniencia.vendas.api.caixa;

import com.conveniencia.vendas.domain.caixa.Caixa;
import com.conveniencia.vendas.domain.caixa.StatusCaixa;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CaixaResponse(
        UUID id,
        String operador,
        Instant aberturaEm,
        BigDecimal fundoTroco,
        StatusCaixa status,
        Instant fechamentoEm) {

    public static CaixaResponse de(Caixa c) {
        return new CaixaResponse(c.id(), c.operador(), c.aberturaEm(),
                c.fundoTroco().valor(), c.status(), c.fechamentoEm());
    }
}
