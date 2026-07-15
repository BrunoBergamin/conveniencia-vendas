package com.conveniencia.vendas.infrastructure.persistence;

import com.conveniencia.vendas.domain.caixa.StatusCaixa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "caixa")
class CaixaEntity {

    @Id
    UUID id;

    @Column(nullable = false)
    String operador;

    @Column(name = "abertura_em", nullable = false)
    Instant aberturaEm;

    @Column(name = "fundo_troco", nullable = false, precision = 12, scale = 2)
    BigDecimal fundoTroco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatusCaixa status;

    @Column(name = "fechamento_em")
    Instant fechamentoEm;

    public CaixaEntity() {
    }
}
