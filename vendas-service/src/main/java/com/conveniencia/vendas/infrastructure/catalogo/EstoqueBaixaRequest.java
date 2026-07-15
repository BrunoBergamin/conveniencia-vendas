package com.conveniencia.vendas.infrastructure.catalogo;

import java.util.List;
import java.util.UUID;

/** Corpo enviado ao catalogo-service em /internal/estoque/baixar. */
public record EstoqueBaixaRequest(List<Item> itens) {
    public record Item(UUID produtoId, int quantidade) {
    }
}
