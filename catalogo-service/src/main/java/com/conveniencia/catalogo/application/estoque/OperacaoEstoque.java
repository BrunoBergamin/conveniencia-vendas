package com.conveniencia.catalogo.application.estoque;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Registro de uma baixa de estoque ja processada, guardado para garantir
 * idempotencia: se a mesma chave chegar de novo (retry do vendas-service),
 * devolvemos a resposta gravada em vez de baixar o estoque outra vez.
 * Tambem e a base da compensacao: o estorno repoe exatamente estes itens.
 */
public record OperacaoEstoque(
        UUID id,
        UUID chave,
        StatusOperacaoEstoque status,
        List<ItemBaixado> itens,
        Instant criadaEm,
        Instant estornadaEm) {

    public static OperacaoEstoque efetivada(UUID chave, List<ItemBaixado> itens) {
        return new OperacaoEstoque(UUID.randomUUID(), chave, StatusOperacaoEstoque.EFETIVADA,
                List.copyOf(itens), Instant.now(), null);
    }

    public OperacaoEstoque estornada() {
        return new OperacaoEstoque(id, chave, StatusOperacaoEstoque.ESTORNADA, itens, criadaEm, Instant.now());
    }
}
