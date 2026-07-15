package com.conveniencia.vendas.application.venda;

import com.conveniencia.vendas.domain.venda.Dinheiro;
import java.util.UUID;

/** Item ja com o preco vigente do catalogo, apos a baixa de estoque. */
public record ItemPrecificado(UUID produtoId, String descricao, Dinheiro precoUnitario, int quantidade) {
}
