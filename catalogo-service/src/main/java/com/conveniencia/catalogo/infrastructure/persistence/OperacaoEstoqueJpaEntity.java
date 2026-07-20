package com.conveniencia.catalogo.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro de baixa de estoque processada (idempotencia). A resposta e gravada
 * em JSON para que um retry com a mesma chave receba EXATAMENTE o que recebeu
 * a primeira vez, sem baixar o estoque de novo.
 */
@Entity
@Table(name = "operacao_estoque")
class OperacaoEstoqueJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID chave;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "resposta_json", nullable = false)
    private String respostaJson;

    @Column(name = "criada_em", nullable = false)
    private Instant criadaEm;

    @Column(name = "estornada_em")
    private Instant estornadaEm;

    protected OperacaoEstoqueJpaEntity() {
    }

    OperacaoEstoqueJpaEntity(UUID id, UUID chave, String status, String respostaJson,
                             Instant criadaEm, Instant estornadaEm) {
        this.id = id;
        this.chave = chave;
        this.status = status;
        this.respostaJson = respostaJson;
        this.criadaEm = criadaEm;
        this.estornadaEm = estornadaEm;
    }

    void marcarEstornada(Instant quando) {
        this.status = "ESTORNADA";
        this.estornadaEm = quando;
    }

    UUID getId() { return id; }
    UUID getChave() { return chave; }
    String getStatus() { return status; }
    String getRespostaJson() { return respostaJson; }
    Instant getCriadaEm() { return criadaEm; }
    Instant getEstornadaEm() { return estornadaEm; }
}
