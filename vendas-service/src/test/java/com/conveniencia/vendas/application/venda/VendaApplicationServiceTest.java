package com.conveniencia.vendas.application.venda;

import com.conveniencia.vendas.domain.caixa.Caixa;
import com.conveniencia.vendas.domain.caixa.CaixaRepository;
import com.conveniencia.vendas.domain.venda.Dinheiro;
import com.conveniencia.vendas.domain.venda.FormaPagamento;
import com.conveniencia.vendas.domain.venda.ItemVenda;
import com.conveniencia.vendas.domain.venda.Venda;
import com.conveniencia.vendas.domain.venda.VendaRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testa a mini-saga do registro de venda: idempotencia pela chave e
 * compensacao (estorno da baixa) quando a persistencia falha.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VendaApplicationServiceTest {

    @Mock
    CaixaRepository caixas;
    @Mock
    VendaRepository vendas;
    @Mock
    CatalogoPort catalogo;

    VendaApplicationService service;

    final UUID chave = UUID.randomUUID();
    final UUID produtoId = UUID.randomUUID();
    final String autorizacao = "Bearer jwt";
    final Caixa caixa = Caixa.abrir("operadora", Dinheiro.de("100.00"));

    @BeforeEach
    void configurar() {
        service = new VendaApplicationService();
        service.caixas = caixas;
        service.vendas = vendas;
        service.catalogo = catalogo;

        when(vendas.buscarPorChave(chave)).thenReturn(Optional.empty());
        when(caixas.buscarAbertoDoOperador("operadora")).thenReturn(Optional.of(caixa));
        when(catalogo.baixarEstoque(eq(chave), any(), anyString())).thenReturn(new ResultadoPrecificacao(
                List.of(new ItemPrecificado(produtoId, "Coca 350ml", Dinheiro.de("5.50"), 2))));
    }

    private Venda registrar() {
        return service.registrar("operadora", chave,
                List.of(new ItemRequisitado(produtoId, 2)), FormaPagamento.PIX, autorizacao);
    }

    @Test
    void chaveJaRegistradaDevolveAVendaExistenteSemChamarOCatalogo() {
        Venda existente = Venda.registrar(chave, caixa.id(),
                List.of(new ItemVenda(produtoId, "Coca 350ml", Dinheiro.de("5.50"), 2)), FormaPagamento.PIX);
        when(vendas.buscarPorChave(chave)).thenReturn(Optional.of(existente));

        assertSame(existente, registrar());
        verify(catalogo, never()).baixarEstoque(any(), any(), anyString());
        verify(vendas, never()).salvar(any());
    }

    @Test
    void falhaAoSalvarDisparaOEstornoDaBaixa() {
        RuntimeException bancoCaiu = new RuntimeException("banco caiu");
        when(vendas.salvar(any())).thenThrow(bancoCaiu);

        RuntimeException lancada = assertThrows(RuntimeException.class, this::registrar);

        assertSame(bancoCaiu, lancada);
        verify(catalogo).estornarBaixa(chave, autorizacao);
    }

    @Test
    void falhaNoProprioEstornoNaoEscondeOErroOriginal() {
        RuntimeException bancoCaiu = new RuntimeException("banco caiu");
        when(vendas.salvar(any())).thenThrow(bancoCaiu);
        doThrow(new RuntimeException("catalogo tambem caiu"))
                .when(catalogo).estornarBaixa(chave, autorizacao);

        RuntimeException lancada = assertThrows(RuntimeException.class, this::registrar);
        assertSame(bancoCaiu, lancada);
    }

    @Test
    void corridaDeChaveDuplicadaDevolveAVendaVencedoraSemEstornar() {
        Venda vencedora = Venda.registrar(chave, caixa.id(),
                List.of(new ItemVenda(produtoId, "Coca 350ml", Dinheiro.de("5.50"), 2)), FormaPagamento.PIX);
        when(vendas.buscarPorChave(chave))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(vencedora));
        when(vendas.salvar(any())).thenThrow(new RuntimeException(
                new ConstraintViolationException("duplicada", new SQLException("23505"),
                        "uk_venda_chave_idempotencia")));

        assertSame(vencedora, registrar());
        // a baixa foi idempotente no catalogo (uma so), entao NAO pode estornar:
        // estornar aqui apagaria a baixa da venda vencedora.
        verify(catalogo, never()).estornarBaixa(any(), anyString());
    }

    @Test
    void fluxoFelizUsaAMesmaChaveNoCatalogoENaVenda() {
        when(vendas.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Venda venda = registrar();

        assertEquals(chave, venda.chaveIdempotencia());
        verify(catalogo).baixarEstoque(eq(chave), any(), eq(autorizacao));
        verify(catalogo, never()).estornarBaixa(any(), anyString());
    }
}
