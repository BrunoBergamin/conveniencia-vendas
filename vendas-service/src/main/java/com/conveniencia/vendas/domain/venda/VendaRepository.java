package com.conveniencia.vendas.domain.venda;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Porta de saida: persistencia e consulta de vendas. */
public interface VendaRepository {
    Venda salvar(Venda venda);
    List<Venda> doDia(LocalDate dia);
    List<Venda> doCaixa(UUID caixaId);
}
