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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueApplicationServiceTest {

    @Mock
    EstoqueRepository estoques;
    @Mock
    ProdutoRepository produtos;
    @InjectMocks
    EstoqueApplicationService service;

    private Produto produto(UUID id) {
        return Produto.reconstituir(id, "789", "Coca", Preco.de("5.50"), Categoria.BEBIDA, true);
    }

    @Test
    void baixarSemSaldoPropagaExcecao() {
        UUID id = UUID.randomUUID();
        when(produtos.buscarPorId(id)).thenReturn(Optional.of(produto(id)));
        when(estoques.buscarPorProduto(id)).thenReturn(Optional.of(Estoque.reconstituir(id, 1)));

        assertThrows(EstoqueInsuficienteException.class,
                () -> service.baixar(List.of(new ItemBaixa(id, 5))));
    }

    @Test
    void baixarAplicaPrecoDoCatalogo() {
        UUID id = UUID.randomUUID();
        when(produtos.buscarPorId(id)).thenReturn(Optional.of(produto(id)));
        when(estoques.buscarPorProduto(id)).thenReturn(Optional.of(Estoque.reconstituir(id, 10)));
        when(estoques.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        ResultadoBaixa resultado = service.baixar(List.of(new ItemBaixa(id, 2)));

        assertEquals(1, resultado.itens().size());
        assertEquals(new BigDecimal("5.50"), resultado.itens().get(0).precoUnitario().valor());
        assertEquals(2, resultado.itens().get(0).quantidade());
    }
}
