package com.conveniencia.catalogo.application.produto;

import com.conveniencia.catalogo.domain.estoque.Estoque;
import com.conveniencia.catalogo.domain.estoque.EstoqueRepository;
import com.conveniencia.catalogo.domain.produto.Preco;
import com.conveniencia.catalogo.domain.produto.Produto;
import com.conveniencia.catalogo.domain.produto.ProdutoRepository;
import com.conveniencia.catalogo.domain.shared.EntidadeNaoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Casos de uso de produto. Orquestra o dominio; a transacao vive aqui. */
@Service
public class ProdutoApplicationService {

    private final ProdutoRepository produtos;
    private final EstoqueRepository estoques;

    public ProdutoApplicationService(ProdutoRepository produtos, EstoqueRepository estoques) {
        this.produtos = produtos;
        this.estoques = estoques;
    }

    @Transactional
    public Produto cadastrar(NovoProduto cmd) {
        if (produtos.existePorCodigoBarras(cmd.codigoBarras())) {
            throw new CodigoBarrasDuplicadoException(cmd.codigoBarras());
        }
        Produto produto = Produto.criar(cmd.codigoBarras(), cmd.descricao(),
                new Preco(cmd.preco()), cmd.categoria());
        Produto salvo = produtos.salvar(produto);
        // todo produto nasce com estoque zerado
        estoques.salvar(Estoque.novo(salvo.id()));
        return salvo;
    }

    @Transactional(readOnly = true)
    public List<Produto> listar() {
        return produtos.listarAtivos();
    }

    @Transactional(readOnly = true)
    public Produto buscar(UUID id) {
        return produtos.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("produto " + id + " nao encontrado"));
    }

    @Transactional
    public Produto atualizar(UUID id, AtualizacaoProduto cmd) {
        Produto produto = buscar(id);
        produto.atualizar(cmd.descricao(), new Preco(cmd.preco()), cmd.categoria());
        return produtos.salvar(produto);
    }

    @Transactional
    public void inativar(UUID id) {
        Produto produto = buscar(id);
        produto.inativar();
        produtos.salvar(produto);
    }
}
