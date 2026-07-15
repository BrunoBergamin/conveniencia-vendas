package com.conveniencia.catalogo.domain.shared;

/** Lancada quando uma entidade referenciada nao existe. */
public class EntidadeNaoEncontradaException extends DomainException {
    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
