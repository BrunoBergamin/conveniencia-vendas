package com.conveniencia.vendas.application.venda;

import java.util.List;

/**
 * Porta de saida para o catalogo-service. Baixa o estoque dos itens e devolve
 * o preco vigente. A implementacao (REST client com fault tolerance) fica na
 * infraestrutura, entao o dominio nao sabe que existe um outro servico.
 */
public interface CatalogoPort {
    ResultadoPrecificacao baixarEstoque(List<ItemRequisitado> itens, String autorizacao);
}
