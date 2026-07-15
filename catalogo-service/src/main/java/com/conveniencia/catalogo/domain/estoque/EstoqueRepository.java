package com.conveniencia.catalogo.domain.estoque;

import java.util.Optional;
import java.util.UUID;

/** Porta de saida: persistencia de Estoque. */
public interface EstoqueRepository {
    Estoque salvar(Estoque estoque);
    Optional<Estoque> buscarPorProduto(UUID produtoId);
}
