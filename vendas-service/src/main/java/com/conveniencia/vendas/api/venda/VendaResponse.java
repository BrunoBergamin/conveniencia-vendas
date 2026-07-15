package com.conveniencia.vendas.api.venda;

import com.conveniencia.vendas.domain.venda.FormaPagamento;
import com.conveniencia.vendas.domain.venda.Venda;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record VendaResponse(
        UUID id,
        UUID caixaId,
        List<Item> itens,
        FormaPagamento formaPagamento,
        BigDecimal total,
        Instant criadaEm) {

    public record Item(UUID produtoId, String descricao, BigDecimal precoUnitario, int quantidade, BigDecimal subtotal) {
    }

    public static VendaResponse de(Venda v) {
        List<Item> itens = v.itens().stream()
                .map(i -> new Item(i.produtoId(), i.descricao(), i.precoUnitario().valor(),
                        i.quantidade(), i.subtotal().valor()))
                .toList();
        return new VendaResponse(v.id(), v.caixaId(), itens, v.formaPagamento(), v.total().valor(), v.criadaEm());
    }
}
