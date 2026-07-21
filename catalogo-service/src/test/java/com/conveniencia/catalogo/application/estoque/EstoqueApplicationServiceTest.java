package com.conveniencia.catalogo.application.estoque;

import com.conveniencia.catalogo.domain.estoque.Estoque;
import com.conveniencia.catalogo.domain.estoque.EstoqueInsuficienteException;
import com.conveniencia.catalogo.domain.estoque.EstoqueRepository;
import com.conveniencia.catalogo.domain.produto.Categoria;
import com.conveniencia.catalogo.domain.produto.Preco;
import com.conveniencia.catalogo.domain.produto.Produto;
import com.conveniencia.catalogo.domain.produto.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueApplicationServiceTest {

    @Mock
    EstoqueRepository estoques;
    @Mock
    ProdutoRepository produtos;
    @Mock
    OperacaoEstoqueRepository operacoes;
    @InjectMocks
    EstoqueApplicationService service;

    private Produto produto(UUID id) {
        return Produto.reconstituir(id, "789", "Coca", Preco.de("5.50"), Categoria.BEBIDA, true);
    }

    @Test
    void baixasEfetivadasConsultamComLimiteDeLote() {
        Instant limite = Instant.parse("2026-07-21T12:00:00Z");
        List<UUID> chaves = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(operacoes.chavesEfetivadasAntesDe(limite, EstoqueApplicationService.MAX_OPERACOES_POR_CONSULTA))
                .thenReturn(chaves);

        assertEquals(chaves, service.baixasEfetivadasAntesDe(limite));
    }

    @Test
    void baixarSemSaldoPropagaExcecao() {
        UUID id = UUID.randomUUID();
        UUID chave = UUID.randomUUID();
        when(operacoes.buscarPorChave(chave)).thenReturn(Optional.empty());
        when(produtos.buscarPorId(id)).thenReturn(Optional.of(produto(id)));
        when(estoques.buscarPorProduto(id)).thenReturn(Optional.of(Estoque.reconstituir(id, 1)));

        assertThrows(EstoqueInsuficienteException.class,
                () -> service.baixar(chave, List.of(new ItemBaixa(id, 5))));
    }

    @Test
    void baixarAplicaPrecoDoCatalogo() {
        UUID id = UUID.randomUUID();
        UUID chave = UUID.randomUUID();
        when(operacoes.buscarPorChave(chave)).thenReturn(Optional.empty());
        when(operacoes.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(produtos.buscarPorId(id)).thenReturn(Optional.of(produto(id)));
        when(estoques.buscarPorProduto(id)).thenReturn(Optional.of(Estoque.reconstituir(id, 10)));
        when(estoques.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        ResultadoBaixa resultado = service.baixar(chave, List.of(new ItemBaixa(id, 2)));

        assertEquals(1, resultado.itens().size());
        assertEquals(new BigDecimal("5.50"), resultado.itens().get(0).precoUnitario().valor());
        assertEquals(2, resultado.itens().get(0).quantidade());
        verify(operacoes).salvar(any());
    }

    @Test
    void baixarComChaveRepetidaDevolveRespostaGravadaSemBaixarDeNovo() {
        UUID id = UUID.randomUUID();
        UUID chave = UUID.randomUUID();
        var gravada = OperacaoEstoque.efetivada(chave,
                List.of(new ItemBaixado(id, "Coca", Preco.de("5.50"), 2)));
        when(operacoes.buscarPorChave(chave)).thenReturn(Optional.of(gravada));

        ResultadoBaixa resultado = service.baixar(chave, List.of(new ItemBaixa(id, 2)));

        assertEquals(gravada.itens(), resultado.itens());
        verify(estoques, never()).salvar(any());
    }

    @Test
    void baixarComChaveEstornadaEhRejeitada() {
        UUID chave = UUID.randomUUID();
        var estornada = OperacaoEstoque.efetivada(chave,
                List.of(new ItemBaixado(UUID.randomUUID(), "Coca", Preco.de("5.50"), 1))).estornada();
        when(operacoes.buscarPorChave(chave)).thenReturn(Optional.of(estornada));

        assertThrows(OperacaoEstornadaException.class,
                () -> service.baixar(chave, List.of(new ItemBaixa(UUID.randomUUID(), 1))));
    }

    @Test
    void estornarRepoeExatamenteOQueABaixaTirou() {
        UUID id = UUID.randomUUID();
        UUID chave = UUID.randomUUID();
        var operacao = OperacaoEstoque.efetivada(chave,
                List.of(new ItemBaixado(id, "Coca", Preco.de("5.50"), 4)));
        when(operacoes.buscarPorChave(chave)).thenReturn(Optional.of(operacao));
        when(operacoes.salvar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(estoques.buscarPorProduto(id)).thenReturn(Optional.of(Estoque.reconstituir(id, 6)));
        when(estoques.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertTrue(service.estornar(chave));

        ArgumentCaptor<Estoque> salvo = ArgumentCaptor.forClass(Estoque.class);
        verify(estoques).salvar(salvo.capture());
        assertEquals(10, salvo.getValue().quantidade());
    }

    @Test
    void estornarDuasVezesNaoRepoeDuasVezes() {
        UUID chave = UUID.randomUUID();
        var jaEstornada = OperacaoEstoque.efetivada(chave,
                List.of(new ItemBaixado(UUID.randomUUID(), "Coca", Preco.de("5.50"), 1))).estornada();
        when(operacoes.buscarPorChave(chave)).thenReturn(Optional.of(jaEstornada));

        assertFalse(service.estornar(chave));
        verify(estoques, never()).salvar(any());
    }

    @Test
    void estornarChaveDesconhecidaEhNoOp() {
        UUID chave = UUID.randomUUID();
        when(operacoes.buscarPorChave(chave)).thenReturn(Optional.empty());

        assertFalse(service.estornar(chave));
        verify(estoques, never()).salvar(any());
    }
}
