package com.conveniencia.vendas.infrastructure.catalogo;

import com.conveniencia.vendas.application.venda.CatalogoIndisponivelException;
import com.conveniencia.vendas.application.venda.CatalogoPort;
import com.conveniencia.vendas.application.venda.ItemPrecificado;
import com.conveniencia.vendas.application.venda.ItemRequisitado;
import com.conveniencia.vendas.application.venda.ResultadoPrecificacao;
import com.conveniencia.vendas.application.venda.SemEstoqueException;
import com.conveniencia.vendas.domain.venda.Dinheiro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Implementa a porta CatalogoPort chamando o catalogo-service via REST.
 * Protegido por timeout, retry e circuit breaker: se o catalogo cair, a venda
 * falha de forma limpa (CatalogoIndisponivel) em vez de travar.
 * Erro de negocio (sem estoque) nao dispara retry nem fallback.
 *
 * O retry so e seguro porque a baixa e IDEMPOTENTE no catalogo pela
 * Idempotency-Key: se o primeiro request baixou mas a resposta se perdeu
 * (timeout), o retry recebe a resposta gravada em vez de baixar de novo.
 */
@ApplicationScoped
public class CatalogoAdapter implements CatalogoPort {

    @Inject
    @RestClient
    CatalogoEstoqueClient client;

    @Override
    @Timeout(2000)
    @Retry(maxRetries = 2, delay = 200, abortOn = SemEstoqueException.class)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "indisponivel", skipOn = SemEstoqueException.class)
    public ResultadoPrecificacao baixarEstoque(UUID chave, List<ItemRequisitado> itens, String autorizacao) {
        EstoqueBaixaRequest req = new EstoqueBaixaRequest(itens.stream()
                .map(i -> new EstoqueBaixaRequest.Item(i.produtoId(), i.quantidade()))
                .toList());
        try {
            EstoqueBaixaResponse resp = client.baixar(autorizacao, chave.toString(), req);
            List<ItemPrecificado> precificados = resp.itens().stream()
                    .map(i -> new ItemPrecificado(i.produtoId(), i.descricao(),
                            new Dinheiro(i.precoUnitario()), i.quantidade()))
                    .toList();
            return new ResultadoPrecificacao(precificados);
        } catch (WebApplicationException e) {
            int status = e.getResponse().getStatus();
            if (status == 409 || status == 404) {
                throw new SemEstoqueException("estoque insuficiente ou produto inexistente no catalogo");
            }
            throw e;
        }
    }

    /**
     * Compensacao: repoe a baixa daquela chave. Idempotente no catalogo, entao
     * o retry aqui tambem e seguro. Sem fallback de proposito: se falhar, o
     * caso de uso loga o alerta de reconciliacao manual.
     */
    @Override
    @Timeout(2000)
    @Retry(maxRetries = 3, delay = 300)
    public void estornarBaixa(UUID chave, String autorizacao) {
        client.estornar(autorizacao, chave.toString());
    }

    /**
     * Consulta do job de reconciliacao. Sem fallback: se o catalogo estiver
     * fora, o job pula a rodada e tenta na proxima.
     */
    @Override
    @Timeout(2000)
    @Retry(maxRetries = 2, delay = 200)
    public List<UUID> baixasEfetivadasAntesDe(Instant limite, String autorizacao) {
        return client.operacoesEfetivadas(autorizacao, limite.toString()).chaves();
    }

    @SuppressWarnings("unused")
    ResultadoPrecificacao indisponivel(UUID chave, List<ItemRequisitado> itens, String autorizacao) {
        throw new CatalogoIndisponivelException();
    }
}
