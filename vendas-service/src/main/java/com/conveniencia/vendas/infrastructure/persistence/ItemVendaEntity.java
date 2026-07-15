package com.conveniencia.vendas.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "item_venda")
class ItemVendaEntity {

    @Id
    UUID id;

    @ManyToOne
    @JoinColumn(name = "venda_id", nullable = false)
    VendaEntity venda;

    @Column(name = "produto_id", nullable = false)
    UUID produtoId;

    @Column(nullable = false)
    String descricao;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    BigDecimal precoUnitario;

    @Column(nullable = false)
    int quantidade;

    public ItemVendaEntity() {
    }
}
