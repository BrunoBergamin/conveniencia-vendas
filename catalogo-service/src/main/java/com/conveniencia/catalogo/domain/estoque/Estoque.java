package com.conveniencia.catalogo.domain.estoque;

import java.util.UUID;

/**
 * Estoque de um produto. Invariante central: a quantidade nunca fica negativa.
 * Essa regra vive aqui, no dominio, e nao no banco nem no controller.
 */
public class Estoque {

    private final UUID produtoId;
    private int quantidade;

    private Estoque(UUID produtoId, int quantidade) {
        if (produtoId == null) {
            throw new IllegalArgumentException("produtoId obrigatorio");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("quantidade nao pode ser negativa");
        }
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public static Estoque novo(UUID produtoId) {
        return new Estoque(produtoId, 0);
    }

    public static Estoque reconstituir(UUID produtoId, int quantidade) {
        return new Estoque(produtoId, quantidade);
    }

    public void darEntrada(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("entrada precisa ser positiva");
        }
        this.quantidade += quantidade;
    }

    public void baixar(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("baixa precisa ser positiva");
        }
        if (quantidade > this.quantidade) {
            throw new EstoqueInsuficienteException(produtoId, this.quantidade, quantidade);
        }
        this.quantidade -= quantidade;
    }

    public boolean temSaldoPara(int quantidade) {
        return quantidade > 0 && quantidade <= this.quantidade;
    }

    public UUID produtoId() { return produtoId; }
    public int quantidade() { return quantidade; }
}
