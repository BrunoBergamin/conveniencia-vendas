package com.conveniencia.catalogo.application.identidade;

import com.conveniencia.catalogo.domain.identidade.Usuario;
import com.conveniencia.catalogo.domain.identidade.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso de login: confere a senha e emite o token. */
@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarios;
    private final SenhaHasher hasher;
    private final EmissorToken emissor;

    public AutenticacaoService(UsuarioRepository usuarios, SenhaHasher hasher, EmissorToken emissor) {
        this.usuarios = usuarios;
        this.hasher = hasher;
        this.emissor = emissor;
    }

    @Transactional(readOnly = true)
    public TokenEmitido autenticar(String login, String senha) {
        Usuario usuario = usuarios.buscarPorLogin(login)
                .orElseThrow(CredenciaisInvalidasException::new);
        if (!hasher.confere(senha, usuario.senhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return emissor.emitir(usuario);
    }
}
