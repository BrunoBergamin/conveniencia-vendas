package com.conveniencia.vendas.infrastructure.persistence;

import com.conveniencia.vendas.domain.venda.FormaPagamento;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "venda")
class VendaEntity {

    @Id
    UUID id;

    @Column(name = "caixa_id", nullable = false)
    UUID caixaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    FormaPagamento formaPagamento;

    @Column(nullable = false, precision = 12, scale = 2)
    BigDecimal total;

    @Column(name = "criada_em", nullable = false)
    Instant criadaEm;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ItemVendaEntity> itens = new ArrayList<>();

    public VendaEntity() {
    }
}
