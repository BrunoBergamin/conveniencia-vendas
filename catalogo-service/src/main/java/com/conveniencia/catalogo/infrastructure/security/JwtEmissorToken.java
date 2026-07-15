package com.conveniencia.catalogo.infrastructure.security;

import com.conveniencia.catalogo.application.identidade.EmissorToken;
import com.conveniencia.catalogo.application.identidade.TokenEmitido;
import com.conveniencia.catalogo.domain.identidade.Usuario;
import org.springframework.stereotype.Component;

/** Adapter da porta EmissorToken usando o JwtService. */
@Component
class JwtEmissorToken implements EmissorToken {

    private final JwtService jwt;

    JwtEmissorToken(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    public TokenEmitido emitir(Usuario usuario) {
        String token = jwt.gerarToken(usuario.login(), usuario.papel().name());
        return new TokenEmitido(token, jwt.expiracaoSegundos());
    }
}
