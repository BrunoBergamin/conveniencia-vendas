package com.conveniencia.vendas.api.relatorio;

import com.conveniencia.vendas.application.relatorio.ResumoVendas;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ResumoVendasResponse(
        int quantidadeVendas,
        BigDecimal total,
        Map<String, BigDecimal> porFormaPagamento,
        List<ProdutoVendido> topProdutos) {

    public record ProdutoVendido(UUID produtoId, String descricao, int quantidade, BigDecimal total) {
    }

    public static ResumoVendasResponse de(ResumoVendas r) {
        Map<String, BigDecimal> porForma = new LinkedHashMap<>();
        r.porFormaPagamento().forEach((forma, valor) -> porForma.put(forma.name(), valor.valor()));

        List<ProdutoVendido> top = r.topProdutos().stream()
                .map(p -> new ProdutoVendido(p.produtoId(), p.descricao(), p.quantidade(), p.total().valor()))
                .toList();

        return new ResumoVendasResponse(r.quantidadeVendas(), r.total().valor(), porForma, top);
    }
}
