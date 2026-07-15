package com.conveniencia.catalogo.domain.produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Objeto de valor de preco. Nunca negativo, sempre com 2 casas.
 */
public record Preco(BigDecimal valor) {

    public Preco {
        Objects.requireNonNull(valor, "preco obrigatorio");
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("preco nao pode ser negativo");
        }
        valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static Preco de(String valor) {
        return new Preco(new BigDecimal(valor));
    }

    public static Preco de(double valor) {
        return new Preco(BigDecimal.valueOf(valor));
    }
}
