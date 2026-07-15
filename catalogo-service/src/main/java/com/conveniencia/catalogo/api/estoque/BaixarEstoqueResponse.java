package com.conveniencia.catalogo.api.estoque;

import com.conveniencia.catalogo.application.estoque.ResultadoBaixa;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Saida da baixa: itens com o preco vigente do catalogo (fonte de verdade). */
public record BaixarEstoqueResponse(List<Item> itens) {

    public record Item(UUID produtoId, String descricao, BigDecimal precoUnitario, int quantidade) {
    }

    public static BaixarEstoqueResponse de(ResultadoBaixa resultado) {
        List<Item> itens = resultado.itens().stream()
                .map(i -> new Item(i.produtoId(), i.descricao(), i.precoUnitario().valor(), i.quantidade()))
                .toList();
        return new BaixarEstoqueResponse(itens);
    }
}
