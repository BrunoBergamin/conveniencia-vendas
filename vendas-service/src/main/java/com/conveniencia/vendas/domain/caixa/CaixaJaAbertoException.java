package com.conveniencia.vendas.domain.caixa;

import com.conveniencia.vendas.domain.shared.DomainException;

/** O operador ja tem um caixa aberto; feche antes de abrir outro. */
public class CaixaJaAbertoException extends DomainException {
    public CaixaJaAbertoException(String operador) {
        super("o operador " + operador + " ja tem um caixa aberto");
    }
}
