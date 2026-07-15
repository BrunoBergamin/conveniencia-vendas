package com.conveniencia.vendas.application.caixa;

import com.conveniencia.vendas.domain.caixa.Caixa;
import com.conveniencia.vendas.domain.caixa.CaixaJaAbertoException;
import com.conveniencia.vendas.domain.caixa.CaixaRepository;
import com.conveniencia.vendas.domain.shared.EntidadeNaoEncontradaException;
import com.conveniencia.vendas.domain.venda.Dinheiro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

/** Casos de uso do caixa: abrir, fechar e consultar o aberto do operador. */
@ApplicationScoped
public class CaixaApplicationService {

    @Inject
    CaixaRepository caixas;

    @Transactional
    public Caixa abrir(String operador, Dinheiro fundoTroco) {
        caixas.buscarAbertoDoOperador(operador).ifPresent(c -> {
            throw new CaixaJaAbertoException(operador);
        });
        return caixas.salvar(Caixa.abrir(operador, fundoTroco));
    }

    @Transactional
    public Caixa fechar(UUID caixaId) {
        Caixa caixa = caixas.buscarPorId(caixaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("caixa " + caixaId + " nao encontrado"));
        caixa.fechar();
        return caixas.salvar(caixa);
    }

    public Optional<Caixa> consultarAberto(String operador) {
        return caixas.buscarAbertoDoOperador(operador);
    }
}
