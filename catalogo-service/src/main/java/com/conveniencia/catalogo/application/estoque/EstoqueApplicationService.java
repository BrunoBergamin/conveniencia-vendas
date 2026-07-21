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

/** Casos de uso de estoque: consultar, dar entrada, baixar na venda e estornar. */
@Service
public class EstoqueApplicationService {

    private final EstoqueRepository estoques;
    private final ProdutoRepository produtos;
    private final OperacaoEstoqueRepository operacoes;

    public EstoqueApplicationService(EstoqueRepository estoques, ProdutoRepository produtos,
                                     OperacaoEstoqueRepository operacoes) {
        this.estoques = estoques;
        this.produtos = produtos;
        this.operacoes = operacoes;
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
     *
     * IDEMPOTENTE pela chave: se a mesma chave chegar de novo (retry apos
     * timeout, por exemplo), devolve a resposta gravada da primeira vez sem
     * baixar o estoque outra vez. E isso que torna o retry automatico do
     * vendas-service seguro. A constraint unique da chave no banco garante a
     * regra mesmo sob concorrencia.
     */
    @Transactional
    public ResultadoBaixa baixar(UUID chave, List<ItemBaixa> itens) {
        var jaProcessada = operacoes.buscarPorChave(chave);
        if (jaProcessada.isPresent()) {
            OperacaoEstoque operacao = jaProcessada.get();
            if (operacao.status() == StatusOperacaoEstoque.ESTORNADA) {
                throw new OperacaoEstornadaException(chave);
            }
            return new ResultadoBaixa(operacao.itens());
        }

        List<ItemBaixado> aplicados = new ArrayList<>();
        for (ItemBaixa item : itens) {
            Produto produto = produtos.buscarPorId(item.produtoId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("produto " + item.produtoId() + " nao encontrado"));
            Estoque estoque = consultar(item.produtoId());
            estoque.baixar(item.quantidade());
            estoques.salvar(estoque);
            aplicados.add(new ItemBaixado(produto.id(), produto.descricao(), produto.preco(), item.quantidade()));
        }
        operacoes.salvar(OperacaoEstoque.efetivada(chave, aplicados));
        return new ResultadoBaixa(aplicados);
    }

    /**
     * Compensacao (saga): estorna uma baixa ja efetivada, repondo exatamente o
     * que ela tirou. Chamado pelo vendas-service quando a venda falha DEPOIS do
     * estoque baixado. Idempotente: estornar duas vezes nao repoe duas vezes, e
     * estornar chave desconhecida e um no-op (a baixa nunca aconteceu).
     */
    @Transactional
    public boolean estornar(UUID chave) {
        var registrada = operacoes.buscarPorChave(chave);
        if (registrada.isEmpty() || registrada.get().status() == StatusOperacaoEstoque.ESTORNADA) {
            return false;
        }
        OperacaoEstoque operacao = registrada.get();
        for (ItemBaixado item : operacao.itens()) {
            Estoque estoque = consultar(item.produtoId());
            estoque.darEntrada(item.quantidade());
            estoques.salvar(estoque);
        }
        operacoes.salvar(operacao.estornada());
        return true;
    }
}
