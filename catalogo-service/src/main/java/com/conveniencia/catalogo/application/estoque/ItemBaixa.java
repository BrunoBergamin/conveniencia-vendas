package com.conveniencia.catalogo.application.estoque;

import java.util.UUID;

/** Item a ter estoque baixado (entrada do caso de uso de baixa). */
public record ItemBaixa(UUID produtoId, int quantidade) {
}
