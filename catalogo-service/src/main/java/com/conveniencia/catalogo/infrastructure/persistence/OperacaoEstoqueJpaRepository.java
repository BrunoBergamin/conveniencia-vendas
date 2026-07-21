package com.conveniencia.catalogo.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface OperacaoEstoqueJpaRepository extends JpaRepository<OperacaoEstoqueJpaEntity, UUID> {
    Optional<OperacaoEstoqueJpaEntity> findByChave(UUID chave);

    @Query("select o.chave from OperacaoEstoqueJpaEntity o " +
           "where o.status = 'EFETIVADA' and o.criadaEm < :limite order by o.criadaEm")
    List<UUID> chavesEfetivadasAntesDe(@Param("limite") Instant limite, Pageable pagina);
}
