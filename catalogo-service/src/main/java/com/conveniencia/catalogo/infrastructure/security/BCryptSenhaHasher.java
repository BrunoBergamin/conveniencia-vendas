package com.conveniencia.catalogo.infrastructure.security;

import com.conveniencia.catalogo.application.identidade.SenhaHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Adapter da porta SenhaHasher usando BCrypt. */
@Component
class BCryptSenhaHasher implements SenhaHasher {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String senhaPura) {
        return encoder.encode(senhaPura);
    }

    @Override
    public boolean confere(String senhaPura, String hash) {
        return encoder.matches(senhaPura, hash);
    }
}
