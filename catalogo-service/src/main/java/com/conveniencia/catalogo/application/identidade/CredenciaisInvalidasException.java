package com.conveniencia.catalogo.application.identidade;

import com.conveniencia.catalogo.domain.shared.DomainException;

/** Login ou senha invalidos. Mensagem generica de proposito (nao vaza qual falhou). */
public class CredenciaisInvalidasException extends DomainException {
    public CredenciaisInvalidasException() {
        super("credenciais invalidas");
    }
}
