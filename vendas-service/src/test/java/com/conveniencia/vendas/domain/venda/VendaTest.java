package com.conveniencia.vendas.domain.venda;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VendaTest {

    @Test
    void totalSomaOsSubtotais() {
        UUID caixa = UUID.randomUUID();
        var itens = List.of(
                new ItemVenda(UUID.randomUUID(), "Coca", Dinheiro.de("5.50"), 2),
                new ItemVenda(UUID.randomUUID(), "Agua", Dinheiro.de("2.50"), 3));

        Venda venda = Venda.registrar(caixa, itens, FormaPagamento.PIX);

        // 2 * 5.50 + 3 * 2.50 = 11.00 + 7.50 = 18.50
        assertEquals(new BigDecimal("18.50"), venda.total().valor());
        assertEquals(caixa, venda.caixaId());
        assertEquals(2, venda.itens().size());
    }

    @Test
    void vendaSemItensRejeitada() {
        assertThrows(IllegalArgumentException.class,
                () -> Venda.registrar(UUID.randomUUID(), List.of(), FormaPagamento.DINHEIRO));
    }
}
