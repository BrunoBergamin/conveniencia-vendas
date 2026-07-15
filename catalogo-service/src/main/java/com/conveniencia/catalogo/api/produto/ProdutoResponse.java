package com.conveniencia.catalogo.api.produto;

import com.conveniencia.catalogo.domain.produto.Categoria;
import com.conveniencia.catalogo.domain.produto.Produto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoResponse(
        UUID id,
        String codigoBarras,
        String descricao,
        BigDecimal preco,
        Categoria categoria,
        boolean ativo) {

    public static ProdutoResponse de(Produto p) {
        return new ProdutoResponse(p.id(), p.codigoBarras(), p.descricao(),
                p.preco().valor(), p.categoria(), p.ativo());
    }
}
