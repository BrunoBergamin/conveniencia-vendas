package com.conveniencia.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface EstoqueJpaRepository extends JpaRepository<EstoqueJpaEntity, UUID> {
}
