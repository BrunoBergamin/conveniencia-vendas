package com.conveniencia.catalogo.infrastructure.persistence;

import com.conveniencia.catalogo.domain.identidade.Papel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** Tabela de usuario. */
@Entity
@Table(name = "usuario")
class UsuarioJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    protected UsuarioJpaEntity() {
    }

    UsuarioJpaEntity(UUID id, String login, String senhaHash, Papel papel) {
        this.id = id;
        this.login = login;
        this.senhaHash = senhaHash;
        this.papel = papel;
    }

    UUID getId() { return id; }
    String getLogin() { return login; }
    String getSenhaHash() { return senhaHash; }
    Papel getPapel() { return papel; }
}
