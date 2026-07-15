package com.conveniencia.vendas.domain.caixa;

import com.conveniencia.vendas.domain.shared.DomainException;

/** Nao ha caixa aberto para o operador registrar a venda. */
public class CaixaNaoAbertoException extends DomainException {
    public CaixaNaoAbertoException(String operador) {
        super("o operador " + operador + " nao tem um caixa aberto");
    }
}
