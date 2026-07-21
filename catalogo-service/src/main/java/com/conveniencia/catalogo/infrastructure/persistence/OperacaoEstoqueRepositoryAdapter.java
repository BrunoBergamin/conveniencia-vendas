package com.conveniencia.catalogo.infrastructure.persistence;

import com.conveniencia.catalogo.application.estoque.ItemBaixado;
import com.conveniencia.catalogo.application.estoque.OperacaoEstoque;
import com.conveniencia.catalogo.application.estoque.OperacaoEstoqueRepository;
import com.conveniencia.catalogo.application.estoque.StatusOperacaoEstoque;
import com.conveniencia.catalogo.domain.produto.Preco;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter JPA da porta OperacaoEstoqueRepository. Os itens baixados sao
 * serializados em JSON (DTO local) para a resposta do replay ser identica
 * a original, sem acoplar o banco aos objetos de dominio.
 */
@Component
class OperacaoEstoqueRepositoryAdapter implements OperacaoEstoqueRepository {

    private final OperacaoEstoqueJpaRepository jpa;
    private final ObjectMapper json;

    OperacaoEstoqueRepositoryAdapter(OperacaoEstoqueJpaRepository jpa, ObjectMapper json) {
        this.jpa = jpa;
        this.json = json;
    }

    /** Forma persistida de um item baixado. */
    record ItemJson(UUID produtoId, String descricao, BigDecimal precoUnitario, int quantidade) {
    }

    @Override
    public Optional<OperacaoEstoque> buscarPorChave(UUID chave) {
        return jpa.findByChave(chave).map(this::paraDominio);
    }

    @Override
    public OperacaoEstoque salvar(OperacaoEstoque operacao) {
        OperacaoEstoqueJpaEntity entity = jpa.findByChave(operacao.chave())
                .map(existente -> {
                    if (operacao.status() == StatusOperacaoEstoque.ESTORNADA) {
                        existente.marcarEstornada(operacao.estornadaEm());
                    }
                    return existente;
                })
                .orElseGet(() -> new OperacaoEstoqueJpaEntity(
                        operacao.id(), operacao.chave(), operacao.status().name(),
                        serializar(operacao.itens()), operacao.criadaEm(), operacao.estornadaEm()));
        return paraDominio(jpa.save(entity));
    }

    private String serializar(List<ItemBaixado> itens) {
        try {
            return json.writeValueAsString(itens.stream()
                    .map(i -> new ItemJson(i.produtoId(), i.descricao(), i.precoUnitario().valor(), i.quantidade()))
                    .toList());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("falha ao serializar itens da operacao de estoque", e);
        }
    }

    private OperacaoEstoque paraDominio(OperacaoEstoqueJpaEntity e) {
        List<ItemJson> itens;
        try {
            itens = json.readValue(e.getRespostaJson(), new TypeReference<List<ItemJson>>() {});
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("falha ao ler itens da operacao " + e.getChave(), ex);
        }
        return new OperacaoEstoque(
                e.getId(), e.getChave(), StatusOperacaoEstoque.valueOf(e.getStatus()),
                itens.stream()
                        .map(i -> new ItemBaixado(i.produtoId(), i.descricao(), new Preco(i.precoUnitario()), i.quantidade()))
                        .toList(),
                e.getCriadaEm(), e.getEstornadaEm());
    }
}
