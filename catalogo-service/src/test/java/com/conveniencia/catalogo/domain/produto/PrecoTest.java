package com.conveniencia.catalogo.domain.produto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrecoTest {

    @Test
    @DisplayName("preco negativo e rejeitado")
    void negativoRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> Preco.de("-0.01"));
    }

    @Test
    @DisplayName("arredonda para 2 casas (HALF_UP)")
    void arredondaDuasCasas() {
        assertEquals(new BigDecimal("5.56"), Preco.de("5.555").valor());
    }

    @Test
    @DisplayName("zero e permitido")
    void zeroPermitido() {
        assertEquals(new BigDecimal("0.00"), Preco.de("0").valor());
    }
}
