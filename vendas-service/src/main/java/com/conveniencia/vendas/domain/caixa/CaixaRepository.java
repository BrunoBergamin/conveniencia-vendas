package com.conveniencia.vendas.domain.caixa;

import java.util.Optional;
import java.util.UUID;

/** Porta de saida: persistencia de Caixa. */
public interface CaixaRepository {
    Caixa salvar(Caixa caixa);
    Optional<Caixa> buscarPorId(UUID id);
    Optional<Caixa> buscarAbertoDoOperador(String operador);
}
