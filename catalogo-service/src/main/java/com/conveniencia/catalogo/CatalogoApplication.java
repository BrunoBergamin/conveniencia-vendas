package com.conveniencia.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * catalogo-service: catalogo de produtos, estoque e identidade (login/JWT).
 * Contexto delimitado "Catalogo e Estoque" do sistema de conveniencia.
 */
@SpringBootApplication
public class CatalogoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogoApplication.class, args);
    }
}
