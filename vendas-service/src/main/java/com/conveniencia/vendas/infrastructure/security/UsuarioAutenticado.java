package com.conveniencia.vendas.infrastructure.security;

import jakarta.enterprise.context.RequestScoped;

/** Guarda o usuario autenticado no escopo da requisicao (setado pelo filtro). */
@RequestScoped
public class UsuarioAutenticado {

    private String login;
    private String papel;
    private String tokenBruto;

    public String login() { return login; }
    public String papel() { return papel; }

    /** Header pronto para repassar o mesmo token ao catalogo-service. */
    public String autorizacao() { return "Bearer " + tokenBruto; }

    void preencher(String login, String papel, String tokenBruto) {
        this.login = login;
        this.papel = papel;
        this.tokenBruto = tokenBruto;
    }
}
