package com.conveniencia.catalogo.domain.produto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de saida: persistencia de Produto. Implementada na infraestrutura. */
public interface ProdutoRepository {
    Produto salvar(Produto produto);
    Optional<Produto> buscarPorId(UUID id);
    Optional<Produto> buscarPorCodigoBarras(String codigoBarras);
    List<Produto> listarAtivos();
    boolean existePorCodigoBarras(String codigoBarras);
}
