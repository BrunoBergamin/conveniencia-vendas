package com.conveniencia.vendas.application.venda;

import com.conveniencia.vendas.domain.shared.DomainException;

/** O catalogo recusou a baixa por falta de estoque de algum item. */
public class SemEstoqueException extends DomainException {
    public SemEstoqueException(String mensagem) {
        super(mensagem);
    }
}
