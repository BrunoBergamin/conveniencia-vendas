package com.conveniencia.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface OperacaoEstoqueJpaRepository extends JpaRepository<OperacaoEstoqueJpaEntity, UUID> {
    Optional<OperacaoEstoqueJpaEntity> findByChave(UUID chave);
}
