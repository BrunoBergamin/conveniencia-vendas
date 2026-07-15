package com.conveniencia.vendas.application.relatorio;

import com.conveniencia.vendas.domain.venda.Dinheiro;
import com.conveniencia.vendas.domain.venda.FormaPagamento;

import java.util.List;
import java.util.Map;

/** Resumo de vendas (do dia ou de um caixa). */
public record ResumoVendas(
        int quantidadeVendas,
        Dinheiro total,
        Map<FormaPagamento, Dinheiro> porFormaPagamento,
        List<ProdutoVendido> topProdutos) {
}
