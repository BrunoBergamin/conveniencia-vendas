package com.conveniencia.catalogo.domain.identidade;

import java.util.UUID;

/** Usuario do sistema. A senha vive sempre como hash, nunca em texto puro. */
public class Usuario {

    private final UUID id;
    private final String login;
    private final String senhaHash;
    private final Papel papel;

    private Usuario(UUID id, String login, String senhaHash, Papel papel) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login obrigatorio");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("senha obrigatoria");
        }
        this.id = id;
        this.login = login.trim();
        this.senhaHash = senhaHash;
        this.papel = papel;
    }

    public static Usuario criar(String login, String senhaHash, Papel papel) {
        return new Usuario(UUID.randomUUID(), login, senhaHash, papel);
    }

    public static Usuario reconstituir(UUID id, String login, String senhaHash, Papel papel) {
        return new Usuario(id, login, senhaHash, papel);
    }

    public UUID id() { return id; }
    public String login() { return login; }
    public String senhaHash() { return senhaHash; }
    public Papel papel() { return papel; }
}
