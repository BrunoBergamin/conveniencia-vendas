package com.conveniencia.catalogo.infrastructure.persistence;

import com.conveniencia.catalogo.domain.estoque.Estoque;
import com.conveniencia.catalogo.domain.estoque.EstoqueRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class EstoqueRepositoryAdapter implements EstoqueRepository {

    private final EstoqueJpaRepository jpa;

    EstoqueRepositoryAdapter(EstoqueJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Estoque salvar(Estoque estoque) {
        // Carrega a linha gerenciada (com sua @Version) quando ja existe, para a
        // trava otimista valer; so cria nova quando o estoque ainda nao existe.
        EstoqueJpaEntity entity = jpa.findById(estoque.produtoId())
                .orElseGet(() -> new EstoqueJpaEntity(estoque.produtoId(), estoque.quantidade()));
        entity.atualizarQuantidade(estoque.quantidade());
        return paraDominio(jpa.save(entity));
    }

    @Override
    public Optional<Estoque> buscarPorProduto(UUID produtoId) {
        return jpa.findById(produtoId).map(EstoqueRepositoryAdapter::paraDominio);
    }

    private static Estoque paraDominio(EstoqueJpaEntity e) {
        return Estoque.reconstituir(e.getProdutoId(), e.getQuantidade());
    }
}
