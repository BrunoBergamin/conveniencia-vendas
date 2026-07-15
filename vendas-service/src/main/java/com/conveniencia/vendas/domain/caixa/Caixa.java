package com.conveniencia.vendas.domain.caixa;

import com.conveniencia.vendas.domain.venda.Dinheiro;

import java.time.Instant;
import java.util.UUID;

/** Caixa (turno) de um operador. Invariante: so registra venda enquanto ABERTO. */
public class Caixa {

    private final UUID id;
    private final String operador;
    private final Instant aberturaEm;
    private final Dinheiro fundoTroco;
    private StatusCaixa status;
    private Instant fechamentoEm;

    private Caixa(UUID id, String operador, Instant aberturaEm, Dinheiro fundoTroco,
                  StatusCaixa status, Instant fechamentoEm) {
        if (operador == null || operador.isBlank()) {
            throw new IllegalArgumentException("operador obrigatorio");
        }
        this.id = id;
        this.operador = operador;
        this.aberturaEm = aberturaEm;
        this.fundoTroco = fundoTroco;
        this.status = status;
        this.fechamentoEm = fechamentoEm;
    }

    public static Caixa abrir(String operador, Dinheiro fundoTroco) {
        return new Caixa(UUID.randomUUID(), operador, Instant.now(), fundoTroco, StatusCaixa.ABERTO, null);
    }

    public static Caixa reconstituir(UUID id, String operador, Instant aberturaEm, Dinheiro fundoTroco,
                                     StatusCaixa status, Instant fechamentoEm) {
        return new Caixa(id, operador, aberturaEm, fundoTroco, status, fechamentoEm);
    }

    public void fechar() {
        if (status == StatusCaixa.FECHADO) {
            throw new CaixaJaFechadoException();
        }
        this.status = StatusCaixa.FECHADO;
        this.fechamentoEm = Instant.now();
    }

    public boolean estaAberto() {
        return status == StatusCaixa.ABERTO;
    }

    public UUID id() { return id; }
    public String operador() { return operador; }
    public Instant aberturaEm() { return aberturaEm; }
    public Dinheiro fundoTroco() { return fundoTroco; }
    public StatusCaixa status() { return status; }
    public Instant fechamentoEm() { return fechamentoEm; }
}
