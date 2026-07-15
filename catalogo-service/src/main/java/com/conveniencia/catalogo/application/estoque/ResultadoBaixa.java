package com.conveniencia.catalogo.application.estoque;

import java.util.List;

/** Resultado do caso de uso de baixa de estoque. */
public record ResultadoBaixa(List<ItemBaixado> itens) {
}
