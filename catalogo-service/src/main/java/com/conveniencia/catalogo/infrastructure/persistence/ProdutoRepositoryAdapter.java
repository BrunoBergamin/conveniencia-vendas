package com.conveniencia.catalogo.infrastructure.persistence;

import com.conveniencia.catalogo.domain.produto.Preco;
import com.conveniencia.catalogo.domain.produto.Produto;
import com.conveniencia.catalogo.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Adapter: liga a porta de dominio (ProdutoRepository) ao Spring Data JPA. */
@Component
class ProdutoRepositoryAdapter implements ProdutoRepository {

    private final ProdutoJpaRepository jpa;

    ProdutoRepositoryAdapter(ProdutoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Produto salvar(Produto produto) {
        return paraDominio(jpa.save(paraJpa(produto)));
    }

    @Override
    public Optional<Produto> buscarPorId(UUID id) {
        return jpa.findById(id).map(ProdutoRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return jpa.findByCodigoBarras(codigoBarras).map(ProdutoRepositoryAdapter::paraDominio);
    }

    @Override
    public List<Produto> listarAtivos() {
        return jpa.findByAtivoTrueOrderByDescricao().stream()
                .map(ProdutoRepositoryAdapter::paraDominio)
                .toList();
    }

    @Override
    public boolean existePorCodigoBarras(String codigoBarras) {
        return jpa.existsByCodigoBarras(codigoBarras);
    }

    private static ProdutoJpaEntity paraJpa(Produto p) {
        return new ProdutoJpaEntity(p.id(), p.codigoBarras(), p.descricao(),
                p.preco().valor(), p.categoria(), p.ativo());
    }

    private static Produto paraDominio(ProdutoJpaEntity e) {
        return Produto.reconstituir(e.getId(), e.getCodigoBarras(), e.getDescricao(),
                new Preco(e.getPreco()), e.getCategoria(), e.isAtivo());
    }
}
