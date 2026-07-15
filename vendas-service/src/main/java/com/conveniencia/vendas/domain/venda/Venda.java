package com.conveniencia.vendas.domain.venda;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Venda registrada em um caixa. O total e calculado a partir dos itens. */
public class Venda {

    private final UUID id;
    private final UUID caixaId;
    private final List<ItemVenda> itens;
    private final FormaPagamento formaPagamento;
    private final Dinheiro total;
    private final Instant criadaEm;

    private Venda(UUID id, UUID caixaId, List<ItemVenda> itens,
                 FormaPagamento formaPagamento, Dinheiro total, Instant criadaEm) {
        this.id = id;
        this.caixaId = caixaId;
        this.itens = List.copyOf(itens);
        this.formaPagamento = formaPagamento;
        this.total = total;
        this.criadaEm = criadaEm;
    }

    public static Venda registrar(UUID caixaId, List<ItemVenda> itens, FormaPagamento formaPagamento) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("a venda precisa de ao menos um item");
        }
        Dinheiro total = itens.stream()
                .map(ItemVenda::subtotal)
                .reduce(Dinheiro.zero(), Dinheiro::somar);
        return new Venda(UUID.randomUUID(), caixaId, itens, formaPagamento, total, Instant.now());
    }

    public static Venda reconstituir(UUID id, UUID caixaId, List<ItemVenda> itens,
                                     FormaPagamento formaPagamento, Dinheiro total, Instant criadaEm) {
        return new Venda(id, caixaId, itens, formaPagamento, total, criadaEm);
    }

    public UUID id() { return id; }
    public UUID caixaId() { return caixaId; }
    public List<ItemVenda> itens() { return itens; }
    public FormaPagamento formaPagamento() { return formaPagamento; }
    public Dinheiro total() { return total; }
    public Instant criadaEm() { return criadaEm; }
}
