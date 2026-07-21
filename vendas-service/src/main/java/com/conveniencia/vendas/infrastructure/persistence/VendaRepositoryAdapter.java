package com.conveniencia.vendas.infrastructure.persistence;

import com.conveniencia.vendas.domain.venda.Dinheiro;
import com.conveniencia.vendas.domain.venda.ItemVenda;
import com.conveniencia.vendas.domain.venda.Venda;
import com.conveniencia.vendas.domain.venda.VendaRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Adapter Panache da porta VendaRepository. */
@ApplicationScoped
class VendaRepositoryAdapter implements VendaRepository, PanacheRepositoryBase<VendaEntity, UUID> {

    /**
     * Transacao curta e local: a chamada remota ao catalogo fica FORA dela.
     * O flush forca o INSERT aqui dentro, entao violacao da chave unique
     * (retry concorrente) estoura nesta chamada e o caso de uso trata.
     */
    @Override
    @Transactional
    public Venda salvar(Venda venda) {
        VendaEntity entity = paraEntidade(venda);
        persist(entity);
        flush();
        return paraDominio(entity);
    }

    @Override
    public Optional<Venda> buscarPorChave(UUID chaveIdempotencia) {
        return find("chaveIdempotencia", chaveIdempotencia).firstResultOptional()
                .map(VendaRepositoryAdapter::paraDominio);
    }

    @Override
    public List<Venda> doDia(LocalDate dia) {
        Instant inicio = dia.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant fim = dia.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return list("criadaEm >= ?1 and criadaEm < ?2", inicio, fim).stream()
                .map(VendaRepositoryAdapter::paraDominio)
                .toList();
    }

    @Override
    public List<Venda> doCaixa(UUID caixaId) {
        return list("caixaId", caixaId).stream()
                .map(VendaRepositoryAdapter::paraDominio)
                .toList();
    }

    private static VendaEntity paraEntidade(Venda v) {
        VendaEntity e = new VendaEntity();
        e.id = v.id();
        e.chaveIdempotencia = v.chaveIdempotencia();
        e.caixaId = v.caixaId();
        e.formaPagamento = v.formaPagamento();
        e.total = v.total().valor();
        e.criadaEm = v.criadaEm();
        for (ItemVenda item : v.itens()) {
            ItemVendaEntity ie = new ItemVendaEntity();
            ie.id = UUID.randomUUID();
            ie.venda = e;
            ie.produtoId = item.produtoId();
            ie.descricao = item.descricao();
            ie.precoUnitario = item.precoUnitario().valor();
            ie.quantidade = item.quantidade();
            e.itens.add(ie);
        }
        return e;
    }

    private static Venda paraDominio(VendaEntity e) {
        List<ItemVenda> itens = e.itens.stream()
                .map(ie -> new ItemVenda(ie.produtoId, ie.descricao, new Dinheiro(ie.precoUnitario), ie.quantidade))
                .toList();
        return Venda.reconstituir(e.id, e.chaveIdempotencia, e.caixaId, itens, e.formaPagamento, new Dinheiro(e.total), e.criadaEm);
    }
}
