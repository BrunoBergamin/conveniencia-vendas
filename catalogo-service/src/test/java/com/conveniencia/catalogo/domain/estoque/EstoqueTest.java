package com.conveniencia.catalogo.domain.estoque;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EstoqueTest {

    private final UUID produtoId = UUID.randomUUID();

    @Test
    @DisplayName("baixa reduz a quantidade")
    void baixaReduz() {
        Estoque estoque = Estoque.reconstituir(produtoId, 10);
        estoque.baixar(3);
        assertEquals(7, estoque.quantidade());
    }

    @Test
    @DisplayName("baixar mais do que existe lanca EstoqueInsuficiente")
    void baixaSemSaldo() {
        Estoque estoque = Estoque.reconstituir(produtoId, 2);
        EstoqueInsuficienteException ex =
                assertThrows(EstoqueInsuficienteException.class, () -> estoque.baixar(5));
        assertEquals(produtoId, ex.produtoId());
        assertEquals(2, estoque.quantidade(), "a quantidade nao pode ter mudado");
    }

    @Test
    @DisplayName("entrada soma a quantidade")
    void entradaSoma() {
        Estoque estoque = Estoque.novo(produtoId);
        estoque.darEntrada(15);
        assertEquals(15, estoque.quantidade());
    }

    @Test
    @DisplayName("nao aceita reconstituir com quantidade negativa")
    void naoAceitaNegativo() {
        assertThrows(IllegalArgumentException.class, () -> Estoque.reconstituir(produtoId, -1));
    }

    @Test
    @DisplayName("baixa e entrada precisam ser positivas")
    void precisaSerPositivo() {
        Estoque estoque = Estoque.reconstituir(produtoId, 5);
        assertThrows(IllegalArgumentException.class, () -> estoque.baixar(0));
        assertThrows(IllegalArgumentException.class, () -> estoque.darEntrada(-3));
    }
}
