package com.conveniencia.vendas.domain.venda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Objeto de valor monetario. Nunca negativo, sempre 2 casas. */
public record Dinheiro(BigDecimal valor) {

    public Dinheiro {
        Objects.requireNonNull(valor, "valor obrigatorio");
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("valor nao pode ser negativo");
        }
        valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinheiro de(String valor) {
        return new Dinheiro(new BigDecimal(valor));
    }

    public static Dinheiro de(double valor) {
        return new Dinheiro(BigDecimal.valueOf(valor));
    }

    public static Dinheiro zero() {
        return new Dinheiro(BigDecimal.ZERO);
    }

    public Dinheiro somar(Dinheiro outro) {
        return new Dinheiro(this.valor.add(outro.valor));
    }

    public Dinheiro multiplicar(int quantidade) {
        return new Dinheiro(this.valor.multiply(BigDecimal.valueOf(quantidade)));
    }
}
