package com.conveniencia.catalogo.api.produto;

import com.conveniencia.catalogo.domain.produto.Categoria;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank String codigoBarras,
        @NotBlank String descricao,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal preco,
        @NotNull Categoria categoria) {
}
