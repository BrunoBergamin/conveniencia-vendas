package com.conveniencia.vendas.application.venda;

import java.util.List;
import java.util.UUID;

/**
 * Porta de saida para o catalogo-service. Baixa o estoque dos itens e devolve
 * o preco vigente. A implementacao (REST client com fault tolerance) fica na
 * infraestrutura, entao o dominio nao sabe que existe um outro servico.
 *
 * A chave identifica a operacao: a baixa e idempotente por chave (retry nao
 * duplica) e o estorno compensa exatamente a baixa daquela chave.
 */
public interface CatalogoPort {
    ResultadoPrecificacao baixarEstoque(UUID chave, List<ItemRequisitado> itens, String autorizacao);
    void estornarBaixa(UUID chave, String autorizacao);
}
