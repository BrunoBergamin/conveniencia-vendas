package com.conveniencia.catalogo.domain.estoque;

import com.conveniencia.catalogo.domain.shared.DomainException;
import java.util.UUID;

/** Tentativa de baixar mais estoque do que existe. */
public class EstoqueInsuficienteException extends DomainException {

    private final UUID produtoId;

    public EstoqueInsuficienteException(UUID produtoId, int disponivel, int solicitado) {
        super("estoque insuficiente para o produto " + produtoId
                + ": disponivel " + disponivel + ", solicitado " + solicitado);
        this.produtoId = produtoId;
    }

    public UUID produtoId() {
        return produtoId;
    }
}
