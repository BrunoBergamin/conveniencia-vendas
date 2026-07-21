package com.conveniencia.catalogo.application.estoque;

import java.util.Optional;
import java.util.UUID;

/** Porta de saida: persistencia do registro de operacoes de baixa (idempotencia). */
public interface OperacaoEstoqueRepository {
    Optional<OperacaoEstoque> buscarPorChave(UUID chave);
    OperacaoEstoque salvar(OperacaoEstoque operacao);
}
