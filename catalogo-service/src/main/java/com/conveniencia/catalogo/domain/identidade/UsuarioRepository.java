package com.conveniencia.catalogo.domain.identidade;

import java.util.Optional;

/** Porta de saida: leitura de usuarios para autenticacao. */
public interface UsuarioRepository {
    Optional<Usuario> buscarPorLogin(String login);
}
