package com.conveniencia.vendas.application.venda;

import com.conveniencia.vendas.domain.caixa.Caixa;
import com.conveniencia.vendas.domain.caixa.CaixaNaoAbertoException;
import com.conveniencia.vendas.domain.caixa.CaixaRepository;
import com.conveniencia.vendas.domain.venda.FormaPagamento;
import com.conveniencia.vendas.domain.venda.ItemVenda;
import com.conveniencia.vendas.domain.venda.Venda;
import com.conveniencia.vendas.domain.venda.VendaRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: registrar uma venda — uma mini-saga de dois passos entre
 * servicos, segura contra retry e contra falha no meio do caminho.
 *
 * 1. Idempotencia: se a chave ja tem venda registrada, devolve essa venda
 *    (retry ou clique duplo nao criam venda em dobro).
 * 2. Baixa de estoque no catalogo (remota, idempotente pela mesma chave,
 *    protegida por timeout/retry/circuit breaker no adapter).
 * 3. Persiste a venda em transacao LOCAL e curta (a chamada remota fica fora).
 * 4. Se a persistencia falhar DEPOIS do estoque baixado, COMPENSA: pede o
 *    estorno da baixa ao catalogo (o passo que desfaz o passo anterior).
 */
@ApplicationScoped
public class VendaApplicationService {

    @Inject
    CaixaRepository caixas;
    @Inject
    VendaRepository vendas;
    @Inject
    CatalogoPort catalogo;

    public Venda registrar(String operador, UUID chaveIdempotencia, List<ItemRequisitado> itens,
                           FormaPagamento formaPagamento, String autorizacao) {
        var jaRegistrada = vendas.buscarPorChave(chaveIdempotencia);
        if (jaRegistrada.isPresent()) {
            return jaRegistrada.get();
        }

        Caixa caixa = caixas.buscarAbertoDoOperador(operador)
                .orElseThrow(() -> new CaixaNaoAbertoException(operador));

        ResultadoPrecificacao precificado = catalogo.baixarEstoque(chaveIdempotencia, itens, autorizacao);

        List<ItemVenda> itensVenda = precificado.itens().stream()
                .map(i -> new ItemVenda(i.produtoId(), i.descricao(), i.precoUnitario(), i.quantidade()))
                .toList();

        Venda venda = Venda.registrar(chaveIdempotencia, caixa.id(), itensVenda, formaPagamento);
        try {
            return vendas.salvar(venda);
        } catch (RuntimeException e) {
            if (ehChaveDuplicada(e)) {
                // corrida: outro request com a mesma chave salvou primeiro; a baixa
                // no catalogo foi uma so (idempotente), entao basta devolver a venda.
                return vendas.buscarPorChave(chaveIdempotencia).orElseThrow(() -> e);
            }
            compensar(chaveIdempotencia, autorizacao);
            throw e;
        }
    }

    /** Desfaz a baixa de estoque quando a venda nao pode ser salva. */
    private void compensar(UUID chave, String autorizacao) {
        try {
            catalogo.estornarBaixa(chave, autorizacao);
            Log.warnf("venda %s falhou apos a baixa; estoque estornado no catalogo", chave);
        } catch (Exception falhaEstorno) {
            // Pior caso da saga: baixa sem venda e sem estorno. Fica gritante no
            // log para reconciliacao manual (ou um job futuro de reconciliacao).
            Log.errorf(falhaEstorno,
                    "RECONCILIAR: baixa %s sem venda registrada e estorno falhou", chave);
        }
    }

    private static boolean ehChaveDuplicada(Throwable e) {
        for (Throwable causa = e; causa != null; causa = causa.getCause()) {
            if (causa instanceof ConstraintViolationException violacao) {
                String constraint = violacao.getConstraintName();
                return constraint == null || constraint.toLowerCase().contains("chave_idempotencia");
            }
        }
        return false;
    }
}
