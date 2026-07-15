package com.conveniencia.vendas.application.relatorio;

import com.conveniencia.vendas.domain.venda.Dinheiro;
import com.conveniencia.vendas.domain.venda.FormaPagamento;
import com.conveniencia.vendas.domain.venda.ItemVenda;
import com.conveniencia.vendas.domain.venda.Venda;
import com.conveniencia.vendas.domain.venda.VendaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Relatorios de vendas: agrega a partir das vendas persistidas. */
@ApplicationScoped
public class RelatorioApplicationService {

    @Inject
    VendaRepository vendas;

    @Transactional
    public ResumoVendas doDia(LocalDate dia) {
        return resumir(vendas.doDia(dia));
    }

    @Transactional
    public ResumoVendas doCaixa(UUID caixaId) {
        return resumir(vendas.doCaixa(caixaId));
    }

    private ResumoVendas resumir(List<Venda> lista) {
        Dinheiro total = lista.stream()
                .map(Venda::total)
                .reduce(Dinheiro.zero(), Dinheiro::somar);

        Map<FormaPagamento, Dinheiro> porForma = new EnumMap<>(FormaPagamento.class);
        for (Venda venda : lista) {
            porForma.merge(venda.formaPagamento(), venda.total(), Dinheiro::somar);
        }

        return new ResumoVendas(lista.size(), total, porForma, topProdutos(lista));
    }

    private List<ProdutoVendido> topProdutos(List<Venda> lista) {
        Map<UUID, Agregado> porProduto = new LinkedHashMap<>();
        for (Venda venda : lista) {
            for (ItemVenda item : venda.itens()) {
                Agregado ag = porProduto.computeIfAbsent(item.produtoId(),
                        id -> new Agregado(item.descricao()));
                ag.quantidade += item.quantidade();
                ag.total = ag.total.somar(item.subtotal());
            }
        }
        return porProduto.entrySet().stream()
                .map(e -> new ProdutoVendido(e.getKey(), e.getValue().descricao,
                        e.getValue().quantidade, e.getValue().total))
                .sorted(Comparator.comparingInt(ProdutoVendido::quantidade).reversed())
                .limit(10)
                .toList();
    }

    private static final class Agregado {
        private final String descricao;
        private int quantidade;
        private Dinheiro total = Dinheiro.zero();

        private Agregado(String descricao) {
            this.descricao = descricao;
        }
    }
}
