package com.conveniencia.vendas.application.venda;

import com.conveniencia.vendas.domain.shared.DomainException;

/** O catalogo-service esta fora do ar ou nao respondeu a tempo (circuit breaker). */
public class CatalogoIndisponivelException extends DomainException {
    public CatalogoIndisponivelException() {
        super("catalogo indisponivel no momento, tente novamente");
    }
}
