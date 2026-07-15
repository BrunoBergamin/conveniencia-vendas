package com.conveniencia.catalogo.application.estoque;

import com.conveniencia.catalogo.domain.estoque.Estoque;
import com.conveniencia.catalogo.domain.estoque.EstoqueRepository;
import com.conveniencia.catalogo.domain.produto.Produto;
import com.conveniencia.catalogo.domain.produto.ProdutoRepository;
import com.conveniencia.catalogo.domain.shared.EntidadeNaoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Casos de uso de estoque: consultar, dar entrada e baixar na venda. */
@Service
public class EstoqueApplicationService {

    private final EstoqueRepository estoques;
    private final ProdutoRepository produtos;

    public EstoqueApplicationService(EstoqueRepository estoques, ProdutoRepository produtos) {
        this.estoques = estoques;
        this.produtos = produtos;
    }

    @Transactional(readOnly = true)
    public Estoque consultar(UUID produtoId) {
        return estoques.buscarPorProduto(produtoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("estoque do produto " + produtoId + " nao encontrado"));
    }

    @Transactional
    public Estoque darEntrada(UUID produtoId, int quantidade) {
        produtos.buscarPorId(produtoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("produto " + produtoId + " nao encontrado"));
        Estoque estoque = consultar(produtoId);
        estoque.darEntrada(quantidade);
        return estoques.salvar(estoque);
    }

    /**
     * Baixa o estoque de varios itens em uma unica transacao (tudo ou nada).
     * Usado pelo vendas-service ao registrar uma venda. Devolve os itens com o
     * preco vigente do catalogo, que e a fonte de verdade do preco.
     */
    @Transactional
    public ResultadoBaixa baixar(List<ItemBaixa> itens) {
        List<ItemBaixado> aplicados = new ArrayList<>();
        for (ItemBaixa item : itens) {
            Produto produto = produtos.buscarPorId(item.produtoId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("produto " + item.produtoId() + " nao encontrado"));
            Estoque estoque = consultar(item.produtoId());
            estoque.baixar(item.quantidade());
            estoques.salvar(estoque);
            aplicados.add(new ItemBaixado(produto.id(), produto.descricao(), produto.preco(), item.quantidade()));
        }
        return new ResultadoBaixa(aplicados);
    }
}
