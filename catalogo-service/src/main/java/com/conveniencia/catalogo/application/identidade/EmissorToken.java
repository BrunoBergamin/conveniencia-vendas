package com.conveniencia.catalogo.application.identidade;

import com.conveniencia.catalogo.domain.identidade.Usuario;

/** Porta de saida: emite um token para um usuario autenticado. */
public interface EmissorToken {
    TokenEmitido emitir(Usuario usuario);
}
