package com.conveniencia.catalogo.domain.shared;

/** Base das excecoes de regra de negocio do dominio. */
public abstract class DomainException extends RuntimeException {
    protected DomainException(String mensagem) {
        super(mensagem);
    }
}
