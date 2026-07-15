package com.conveniencia.catalogo.application.estoque;

import com.conveniencia.catalogo.domain.produto.Preco;
import java.util.UUID;

/** Item apos a baixa, com o preco vigente do catalogo (fonte de verdade). */
public record ItemBaixado(UUID produtoId, String descricao, Preco precoUnitario, int quantidade) {
}
