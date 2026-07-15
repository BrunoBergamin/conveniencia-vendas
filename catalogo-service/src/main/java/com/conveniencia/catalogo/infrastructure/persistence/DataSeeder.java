package com.conveniencia.catalogo.infrastructure.persistence;

import com.conveniencia.catalogo.application.identidade.SenhaHasher;
import com.conveniencia.catalogo.domain.identidade.Papel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Cria os usuarios padrao no primeiro boot (a senha precisa de hash BCrypt,
 * por isso nao vai no seed SQL). So roda com a tabela vazia.
 */
@Component
class DataSeeder implements ApplicationRunner {

    private final UsuarioJpaRepository usuarios;
    private final SenhaHasher hasher;

    DataSeeder(UsuarioJpaRepository usuarios, SenhaHasher hasher) {
        this.usuarios = usuarios;
        this.hasher = hasher;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarios.count() > 0) {
            return;
        }
        usuarios.save(new UsuarioJpaEntity(UUID.randomUUID(), "gerente",
                hasher.hash("gerente123"), Papel.GERENTE));
        usuarios.save(new UsuarioJpaEntity(UUID.randomUUID(), "operador",
                hasher.hash("operador123"), Papel.OPERADOR));
    }
}
