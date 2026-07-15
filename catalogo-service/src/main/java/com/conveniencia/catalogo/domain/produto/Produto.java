package com.conveniencia.catalogo.domain.produto;

import java.util.UUID;

/**
 * Produto do catalogo. Entidade de dominio pura (sem framework).
 * As invariantes ficam aqui: descricao e codigo de barras obrigatorios.
 */
public class Produto {

    private final UUID id;
    private String codigoBarras;
    private String descricao;
    private Preco preco;
    private Categoria categoria;
    private boolean ativo;

    private Produto(UUID id, String codigoBarras, String descricao,
                    Preco preco, Categoria categoria, boolean ativo) {
        this.id = id;
        this.ativo = ativo;
        definirCodigoBarras(codigoBarras);
        definirDescricao(descricao);
        this.preco = preco;
        this.categoria = categoria;
    }

    /** Cria um produto novo (id gerado, ativo). */
    public static Produto criar(String codigoBarras, String descricao,
                                Preco preco, Categoria categoria) {
        return new Produto(UUID.randomUUID(), codigoBarras, descricao, preco, categoria, true);
    }

    /** Reconstitui um produto ja existente (vindo do banco). */
    public static Produto reconstituir(UUID id, String codigoBarras, String descricao,
                                       Preco preco, Categoria categoria, boolean ativo) {
        return new Produto(id, codigoBarras, descricao, preco, categoria, ativo);
    }

    public void atualizar(String descricao, Preco preco, Categoria categoria) {
        definirDescricao(descricao);
        this.preco = preco;
        this.categoria = categoria;
    }

    public void inativar() {
        this.ativo = false;
    }

    private void definirDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao obrigatoria");
        }
        this.descricao = descricao.trim();
    }

    private void definirCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("codigo de barras obrigatorio");
        }
        this.codigoBarras = codigoBarras.trim();
    }

    public UUID id() { return id; }
    public String codigoBarras() { return codigoBarras; }
    public String descricao() { return descricao; }
    public Preco preco() { return preco; }
    public Categoria categoria() { return categoria; }
    public boolean ativo() { return ativo; }
}
