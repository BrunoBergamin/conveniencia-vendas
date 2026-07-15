package com.conveniencia.vendas.infrastructure.persistence;

import com.conveniencia.vendas.domain.caixa.Caixa;
import com.conveniencia.vendas.domain.caixa.CaixaRepository;
import com.conveniencia.vendas.domain.caixa.StatusCaixa;
import com.conveniencia.vendas.domain.venda.Dinheiro;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

/** Adapter Panache da porta CaixaRepository. */
@ApplicationScoped
class CaixaRepositoryAdapter implements CaixaRepository, PanacheRepositoryBase<CaixaEntity, UUID> {

    @Override
    public Caixa salvar(Caixa caixa) {
        CaixaEntity entity = findById(caixa.id());
        if (entity == null) {
            entity = new CaixaEntity();
            entity.id = caixa.id();
            entity.operador = caixa.operador();
            entity.aberturaEm = caixa.aberturaEm();
            entity.fundoTroco = caixa.fundoTroco().valor();
            entity.status = caixa.status();
            entity.fechamentoEm = caixa.fechamentoEm();
            persist(entity);
        } else {
            entity.status = caixa.status();
            entity.fechamentoEm = caixa.fechamentoEm();
        }
        return paraDominio(entity);
    }

    @Override
    public Optional<Caixa> buscarPorId(UUID id) {
        return findByIdOptional(id).map(CaixaRepositoryAdapter::paraDominio);
    }

    @Override
    public Optional<Caixa> buscarAbertoDoOperador(String operador) {
        return find("operador = ?1 and status = ?2", operador, StatusCaixa.ABERTO)
                .firstResultOptional()
                .map(CaixaRepositoryAdapter::paraDominio);
    }

    private static Caixa paraDominio(CaixaEntity e) {
        return Caixa.reconstituir(e.id, e.operador, e.aberturaEm, new Dinheiro(e.fundoTroco),
                e.status, e.fechamentoEm);
    }
}
