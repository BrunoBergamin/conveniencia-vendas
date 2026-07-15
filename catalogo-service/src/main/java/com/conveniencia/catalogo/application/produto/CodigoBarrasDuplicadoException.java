package com.conveniencia.catalogo.application.produto;

import com.conveniencia.catalogo.domain.shared.DomainException;

/** Ja existe um produto com o mesmo codigo de barras. */
public class CodigoBarrasDuplicadoException extends DomainException {
    public CodigoBarrasDuplicadoException(String codigoBarras) {
        super("ja existe produto com o codigo de barras " + codigoBarras);
    }
}
