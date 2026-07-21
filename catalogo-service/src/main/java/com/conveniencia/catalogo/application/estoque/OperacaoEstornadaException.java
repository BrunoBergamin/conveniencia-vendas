package com.conveniencia.catalogo.application.estoque;

import com.conveniencia.catalogo.domain.shared.DomainException;

import java.util.UUID;

/**
 * A chave chegou de novo depois de a operacao ter sido estornada (a venda
 * correspondente falhou e foi compensada). Rebaixar aqui duplicaria estoque
 * perdido, entao o retry tardio e rejeitado.
 */
public class OperacaoEstornadaException extends DomainException {
    public OperacaoEstornadaException(UUID chave) {
        super("operacao " + chave + " ja foi estornada; a venda deve ser refeita com nova chave");
    }
}
