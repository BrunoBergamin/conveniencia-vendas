package com.conveniencia.catalogo.infrastructure.persistence;

import com.conveniencia.catalogo.domain.identidade.Usuario;
import com.conveniencia.catalogo.domain.identidade.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpa;

    UsuarioRepositoryAdapter(UsuarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return jpa.findByLogin(login).map(e ->
                Usuario.reconstituir(e.getId(), e.getLogin(), e.getSenhaHash(), e.getPapel()));
    }
}
