package com.conveniencia.vendas.domain.venda;

import java.util.UUID;

/** Item de uma venda. O preco vem do catalogo (fonte de verdade), nunca do cliente. */
public class ItemVenda {

    private final UUID produtoId;
    private final String descricao;
    private final Dinheiro precoUnitario;
    private final int quantidade;

    public ItemVenda(UUID produtoId, String descricao, Dinheiro precoUnitario, int quantidade) {
        if (produtoId == null) {
            throw new IllegalArgumentException("produtoId obrigatorio");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("quantidade precisa ser positiva");
        }
        this.produtoId = produtoId;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
    }

    public Dinheiro subtotal() {
        return precoUnitario.multiplicar(quantidade);
    }

    public UUID produtoId() { return produtoId; }
    public String descricao() { return descricao; }
    public Dinheiro precoUnitario() { return precoUnitario; }
    public int quantidade() { return quantidade; }
}
