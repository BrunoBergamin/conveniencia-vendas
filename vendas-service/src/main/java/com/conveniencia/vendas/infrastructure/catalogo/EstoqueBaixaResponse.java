package com.conveniencia.vendas.infrastructure.catalogo;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Resposta do catalogo-service com os itens ja precificados. */
public record EstoqueBaixaResponse(List<Item> itens) {
    public record Item(UUID produtoId, String descricao, BigDecimal precoUnitario, int quantidade) {
    }
}
