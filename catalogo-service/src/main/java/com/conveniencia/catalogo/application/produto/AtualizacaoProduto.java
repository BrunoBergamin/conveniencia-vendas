package com.conveniencia.catalogo.application.produto;

import com.conveniencia.catalogo.domain.produto.Categoria;
import java.math.BigDecimal;

/** Comando de entrada para atualizar um produto. */
public record AtualizacaoProduto(String descricao, BigDecimal preco, Categoria categoria) {
}
