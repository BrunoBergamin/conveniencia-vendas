package com.conveniencia.vendas.domain.shared;

public class EntidadeNaoEncontradaException extends DomainException {
    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
