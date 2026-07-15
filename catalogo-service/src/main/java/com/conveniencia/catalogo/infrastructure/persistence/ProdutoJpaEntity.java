package com.conveniencia.catalogo.infrastructure.persistence;

import com.conveniencia.catalogo.domain.produto.Categoria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

/** Tabela de produto. Detalhe de infraestrutura, separado da entidade de dominio. */
@Entity
@Table(name = "produto")
class ProdutoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "codigo_barras", nullable = false, unique = true)
    private String codigoBarras;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private boolean ativo;

    protected ProdutoJpaEntity() {
    }

    ProdutoJpaEntity(UUID id, String codigoBarras, String descricao, BigDecimal preco, Categoria categoria, boolean ativo) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.ativo = ativo;
    }

    UUID getId() { return id; }
    String getCodigoBarras() { return codigoBarras; }
    String getDescricao() { return descricao; }
    BigDecimal getPreco() { return preco; }
    Categoria getCategoria() { return categoria; }
    boolean isAtivo() { return ativo; }
}
