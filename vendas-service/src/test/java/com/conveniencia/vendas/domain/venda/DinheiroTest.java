package com.conveniencia.vendas.domain.venda;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DinheiroTest {

    @Test
    void negativoRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> Dinheiro.de("-1"));
    }

    @Test
    void somaEMultiplicacao() {
        assertEquals(new BigDecimal("15.00"), Dinheiro.de("5.00").multiplicar(3).valor());
        assertEquals(new BigDecimal("7.50"), Dinheiro.de("5.00").somar(Dinheiro.de("2.50")).valor());
    }

    @Test
    void arredondaDuasCasas() {
        assertEquals(new BigDecimal("2.35"), Dinheiro.de("2.345").valor());
    }
}
