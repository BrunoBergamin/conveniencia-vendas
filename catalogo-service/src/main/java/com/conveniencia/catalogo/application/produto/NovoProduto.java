package com.conveniencia.catalogo.application.produto;

import com.conveniencia.catalogo.domain.produto.Categoria;
import java.math.BigDecimal;

/** Comando de entrada para cadastrar um produto. */
public record NovoProduto(String codigoBarras, String descricao, BigDecimal preco, Categoria categoria) {
}
