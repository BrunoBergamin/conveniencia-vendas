package com.conveniencia.vendas.api.caixa;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AbrirCaixaRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal fundoTroco) {
}
