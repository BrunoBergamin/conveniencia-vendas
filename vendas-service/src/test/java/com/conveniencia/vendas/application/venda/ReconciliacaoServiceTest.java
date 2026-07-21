package com.conveniencia.vendas.application.venda;

import com.conveniencia.vendas.domain.venda.Dinheiro;
import com.conveniencia.vendas.domain.venda.FormaPagamento;
import com.conveniencia.vendas.domain.venda.ItemVenda;
import com.conveniencia.vendas.domain.venda.Venda;
import com.conveniencia.vendas.domain.venda.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testa a rede de seguranca da saga: baixa com venda fica em paz, baixa orfa
 * e estornada, e falha em um estorno nao derruba o resto da rodada.
 */
@ExtendWith(MockitoExtension.class)
class ReconciliacaoServiceTest {

    @Mock
    VendaRepository vendas;
    @Mock
    CatalogoPort catalogo;

    ReconciliacaoService service;

    final Instant limite = Instant.parse("2026-07-21T12:00:00Z");
    final String autorizacao = "Bearer token-de-servico";

    @BeforeEach
    void configurar() {
        service = new ReconciliacaoService();
        service.vendas = vendas;
        service.catalogo = catalogo;
    }

    private Venda venda(UUID chave) {
        return Venda.registrar(chave, UUID.randomUUID(),
                List.of(new ItemVenda(UUID.randomUUID(), "Coca 350ml", Dinheiro.de("5.50"), 1)),
                FormaPagamento.PIX);
    }

    @Test
    void baixaComVendaCorrespondenteNaoEstorna() {
        UUID chave = UUID.randomUUID();
        when(catalogo.baixasEfetivadasAntesDe(limite, autorizacao)).thenReturn(List.of(chave));
        when(vendas.buscarPorChave(chave)).thenReturn(Optional.of(venda(chave)));

        var resultado = service.reconciliar(limite, autorizacao);

        assertEquals(new ReconciliacaoService.Resultado(1, 1, 0, 0), resultado);
        verify(catalogo, never()).estornarBaixa(any(), anyString());
    }

    @Test
    void baixaOrfaEhEstornada() {
        UUID orfa = UUID.randomUUID();
        when(catalogo.baixasEfetivadasAntesDe(limite, autorizacao)).thenReturn(List.of(orfa));
        when(vendas.buscarPorChave(orfa)).thenReturn(Optional.empty());

        var resultado = service.reconciliar(limite, autorizacao);

        assertEquals(new ReconciliacaoService.Resultado(1, 0, 1, 0), resultado);
        verify(catalogo).estornarBaixa(orfa, autorizacao);
    }

    @Test
    void falhaEmUmEstornoNaoInterrompeORestoDaRodada() {
        UUID primeira = UUID.randomUUID();
        UUID segunda = UUID.randomUUID();
        when(catalogo.baixasEfetivadasAntesDe(limite, autorizacao)).thenReturn(List.of(primeira, segunda));
        when(vendas.buscarPorChave(any())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("catalogo instavel"))
                .when(catalogo).estornarBaixa(eq(primeira), anyString());

        var resultado = service.reconciliar(limite, autorizacao);

        assertEquals(new ReconciliacaoService.Resultado(2, 0, 1, 1), resultado);
        verify(catalogo).estornarBaixa(segunda, autorizacao);
    }
}
