package com.conveniencia.catalogo.application.estoque;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de saida: persistencia do registro de operacoes de baixa (idempotencia). */
public interface OperacaoEstoqueRepository {
    Optional<OperacaoEstoque> buscarPorChave(UUID chave);
    OperacaoEstoque salvar(OperacaoEstoque operacao);

    /**
     * Chaves de baixas EFETIVADAS criadas antes do limite, das mais antigas para
     * as mais novas. Alimenta o job de reconciliacao do vendas-service.
     */
    List<UUID> chavesEfetivadasAntesDe(Instant limite, int max);
}
