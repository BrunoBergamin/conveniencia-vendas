package com.conveniencia.catalogo.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.UUID;

/** Tabela de estoque (1 para 1 com produto). @Version da trava otimista contra concorrencia. */
@Entity
@Table(name = "estoque")
class EstoqueJpaEntity {

    @Id
    @Column(name = "produto_id")
    private UUID produtoId;

    @Column(nullable = false)
    private int quantidade;

    @Version
    private Long versao;

    protected EstoqueJpaEntity() {
    }

    EstoqueJpaEntity(UUID produtoId, int quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    void atualizarQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    UUID getProdutoId() { return produtoId; }
    int getQuantidade() { return quantidade; }
}
