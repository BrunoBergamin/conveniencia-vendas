package com.conveniencia.vendas.api.venda;

import com.conveniencia.vendas.domain.venda.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record RegistrarVendaRequest(
        @NotNull UUID chaveIdempotencia,
        @NotEmpty List<@Valid Item> itens,
        @NotNull FormaPagamento formaPagamento) {

    public record Item(
            @NotNull UUID produtoId,
            @Positive int quantidade) {
    }
}
