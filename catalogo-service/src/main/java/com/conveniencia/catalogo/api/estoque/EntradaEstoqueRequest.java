package com.conveniencia.catalogo.api.estoque;

import jakarta.validation.constraints.Positive;

public record EntradaEstoqueRequest(@Positive int quantidade) {
}
