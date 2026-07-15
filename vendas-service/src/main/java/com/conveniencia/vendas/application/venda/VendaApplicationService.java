package com.conveniencia.vendas.application.venda;

import com.conveniencia.vendas.domain.caixa.Caixa;
import com.conveniencia.vendas.domain.caixa.CaixaNaoAbertoException;
import com.conveniencia.vendas.domain.caixa.CaixaRepository;
import com.conveniencia.vendas.domain.venda.FormaPagamento;
import com.conveniencia.vendas.domain.venda.ItemVenda;
import com.conveniencia.vendas.domain.venda.Venda;
import com.conveniencia.vendas.domain.venda.VendaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Caso de uso: registrar uma venda.
 * Passos: achar o caixa aberto do operador, pedir a baixa de estoque ao catalogo
 * (que devolve os precos), montar a venda com esses precos e salvar.
 */
@ApplicationScoped
public class VendaApplicationService {

    @Inject
    CaixaRepository caixas;
    @Inject
    VendaRepository vendas;
    @Inject
    CatalogoPort catalogo;

    @Transactional
    public Venda registrar(String operador, List<ItemRequisitado> itens,
                           FormaPagamento formaPagamento, String autorizacao) {
        Caixa caixa = caixas.buscarAbertoDoOperador(operador)
                .orElseThrow(() -> new CaixaNaoAbertoException(operador));

        ResultadoPrecificacao precificado = catalogo.baixarEstoque(itens, autorizacao);

        List<ItemVenda> itensVenda = precificado.itens().stream()
                .map(i -> new ItemVenda(i.produtoId(), i.descricao(), i.precoUnitario(), i.quantidade()))
                .toList();

        Venda venda = Venda.registrar(caixa.id(), itensVenda, formaPagamento);
        return vendas.salvar(venda);
    }
}
