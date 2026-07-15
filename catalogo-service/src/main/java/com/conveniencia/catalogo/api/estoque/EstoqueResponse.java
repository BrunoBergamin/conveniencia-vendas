package com.conveniencia.catalogo.api.estoque;

import com.conveniencia.catalogo.domain.estoque.Estoque;

import java.util.UUID;

public record EstoqueResponse(UUID produtoId, int quantidade) {
    public static EstoqueResponse de(Estoque e) {
        return new EstoqueResponse(e.produtoId(), e.quantidade());
    }
}
