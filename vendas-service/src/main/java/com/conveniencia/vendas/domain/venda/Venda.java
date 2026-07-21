package com.conveniencia.vendas.domain.venda;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Venda registrada em um caixa. O total e calculado a partir dos itens.
 * A chave de idempotencia identifica a TENTATIVA de venda: repetir a mesma
 * chave nao pode gerar segunda venda (constraint unique no banco).
 */
public class Venda {

    private final UUID id;
    private final UUID chaveIdempotencia;
    private final UUID caixaId;
    private final List<ItemVenda> itens;
    private final FormaPagamento formaPagamento;
    private final Dinheiro total;
    private final Instant criadaEm;

    private Venda(UUID id, UUID chaveIdempotencia, UUID caixaId, List<ItemVenda> itens,
                 FormaPagamento formaPagamento, Dinheiro total, Instant criadaEm) {
        this.id = id;
        this.chaveIdempotencia = chaveIdempotencia;
        this.caixaId = caixaId;
        this.itens = List.copyOf(itens);
        this.formaPagamento = formaPagamento;
        this.total = total;
        this.criadaEm = criadaEm;
    }

    public static Venda registrar(UUID chaveIdempotencia, UUID caixaId, List<ItemVenda> itens,
                                  FormaPagamento formaPagamento) {
        if (chaveIdempotencia == null) {
            throw new IllegalArgumentException("a venda precisa de chave de idempotencia");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("a venda precisa de ao menos um item");
        }
        Dinheiro total = itens.stream()
                .map(ItemVenda::subtotal)
                .reduce(Dinheiro.zero(), Dinheiro::somar);
        return new Venda(UUID.randomUUID(), chaveIdempotencia, caixaId, itens, formaPagamento, total, Instant.now());
    }

    public static Venda reconstituir(UUID id, UUID chaveIdempotencia, UUID caixaId, List<ItemVenda> itens,
                                     FormaPagamento formaPagamento, Dinheiro total, Instant criadaEm) {
        return new Venda(id, chaveIdempotencia, caixaId, itens, formaPagamento, total, criadaEm);
    }

    public UUID id() { return id; }
    public UUID chaveIdempotencia() { return chaveIdempotencia; }
    public UUID caixaId() { return caixaId; }
    public List<ItemVenda> itens() { return itens; }
    public FormaPagamento formaPagamento() { return formaPagamento; }
    public Dinheiro total() { return total; }
    public Instant criadaEm() { return criadaEm; }
}
