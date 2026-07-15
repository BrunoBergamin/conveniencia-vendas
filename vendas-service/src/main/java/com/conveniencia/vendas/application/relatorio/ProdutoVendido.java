package com.conveniencia.vendas.application.relatorio;

import com.conveniencia.vendas.domain.venda.Dinheiro;
import java.util.UUID;

public record ProdutoVendido(UUID produtoId, String descricao, int quantidade, Dinheiro total) {
}
