package com.conveniencia.catalogo.api.estoque;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

/** Entrada do endpoint interno de baixa (consumido pelo vendas-service). */
public record BaixarEstoqueRequest(
        @NotEmpty List<@Valid Item> itens) {

    public record Item(
            @NotNull UUID produtoId,
            @Positive int quantidade) {
    }
}
