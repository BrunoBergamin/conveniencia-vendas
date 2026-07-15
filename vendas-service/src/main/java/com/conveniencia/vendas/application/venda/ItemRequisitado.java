package com.conveniencia.vendas.application.venda;

import java.util.UUID;

/** Item pedido na venda (o cliente informa produto e quantidade, nunca o preco). */
public record ItemRequisitado(UUID produtoId, int quantidade) {
}
