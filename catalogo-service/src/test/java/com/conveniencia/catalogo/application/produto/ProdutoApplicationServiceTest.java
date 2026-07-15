package com.conveniencia.catalogo.application.produto;

import com.conveniencia.catalogo.domain.estoque.EstoqueRepository;
import com.conveniencia.catalogo.domain.produto.Categoria;
import com.conveniencia.catalogo.domain.produto.Produto;
import com.conveniencia.catalogo.domain.produto.ProdutoRepository;
import com.conveniencia.catalogo.domain.shared.EntidadeNaoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoApplicationServiceTest {

    @Mock
    ProdutoRepository produtos;
    @Mock
    EstoqueRepository estoques;
    @InjectMocks
    ProdutoApplicationService service;

    @Test
    void cadastrarCriaProdutoEEstoqueZerado() {
        when(produtos.existePorCodigoBarras("789")).thenReturn(false);
        when(produtos.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        Produto p = service.cadastrar(new NovoProduto("789", "Coca", new BigDecimal("5.50"), Categoria.BEBIDA));

        assertEquals("Coca", p.descricao());
        verify(estoques).salvar(any());
    }

    @Test
    void cadastrarComCodigoDuplicadoLanca() {
        when(produtos.existePorCodigoBarras("789")).thenReturn(true);
        assertThrows(CodigoBarrasDuplicadoException.class, () ->
                service.cadastrar(new NovoProduto("789", "Coca", BigDecimal.ONE, Categoria.BEBIDA)));
    }

    @Test
    void buscarInexistenteLanca() {
        when(produtos.buscarPorId(any())).thenReturn(Optional.empty());
        assertThrows(EntidadeNaoEncontradaException.class, () -> service.buscar(UUID.randomUUID()));
    }
}
