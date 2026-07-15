package com.conveniencia.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ProdutoJpaRepository extends JpaRepository<ProdutoJpaEntity, UUID> {
    Optional<ProdutoJpaEntity> findByCodigoBarras(String codigoBarras);
    boolean existsByCodigoBarras(String codigoBarras);
    List<ProdutoJpaEntity> findByAtivoTrueOrderByDescricao();
}
