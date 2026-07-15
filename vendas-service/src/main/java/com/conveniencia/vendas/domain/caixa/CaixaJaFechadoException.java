package com.conveniencia.vendas.domain.caixa;

import com.conveniencia.vendas.domain.shared.DomainException;

/** Tentativa de fechar um caixa que ja esta fechado. */
public class CaixaJaFechadoException extends DomainException {
    public CaixaJaFechadoException() {
        super("caixa ja esta fechado");
    }
}
