package com.conveniencia.vendas.domain.caixa;

import com.conveniencia.vendas.domain.venda.Dinheiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaixaTest {

    @Test
    void abreAbertoEFechaFechado() {
        Caixa caixa = Caixa.abrir("operador", Dinheiro.de("100.00"));
        assertTrue(caixa.estaAberto());

        caixa.fechar();
        assertFalse(caixa.estaAberto());
    }

    @Test
    void fecharDuasVezesLanca() {
        Caixa caixa = Caixa.abrir("operador", Dinheiro.zero());
        caixa.fechar();
        assertThrows(CaixaJaFechadoException.class, caixa::fechar);
    }
}
