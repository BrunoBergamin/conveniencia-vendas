package com.conveniencia.catalogo.api.auth;

import com.conveniencia.catalogo.application.identidade.AutenticacaoService;
import com.conveniencia.catalogo.application.identidade.TokenEmitido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticacao")
public class AuthController {

    private final AutenticacaoService autenticacao;

    public AuthController(AutenticacaoService autenticacao) {
        this.autenticacao = autenticacao;
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica e devolve um JWT")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        TokenEmitido token = autenticacao.autenticar(req.login(), req.senha());
        return new LoginResponse(token.token(), token.expiraEmSegundos());
    }
}
