package com.conveniencia.vendas.infrastructure.reconciliacao;

import com.conveniencia.vendas.application.venda.ReconciliacaoService;
import com.conveniencia.vendas.infrastructure.security.TokenServico;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;

/**
 * Dispara a reconciliacao periodicamente. A idade minima e o que evita mexer
 * em vendas em andamento: o fluxo normal leva segundos, entao uma baixa com
 * varios minutos e sem venda so pode ser orfa.
 */
@ApplicationScoped
public class ReconciliacaoScheduler {

    @Inject
    ReconciliacaoService reconciliacao;
    @Inject
    TokenServico token;
    @Inject
    MeterRegistry metrics;

    @ConfigProperty(name = "reconciliacao.idade-minima", defaultValue = "PT5M")
    Duration idadeMinima;

    @Scheduled(every = "{reconciliacao.intervalo}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void executar() {
        try {
            var resultado = reconciliacao.reconciliar(Instant.now().minus(idadeMinima), token.bearer());
            metrics.counter("reconciliacao_baixas_estornadas_total").increment(resultado.estornadas());
            metrics.counter("reconciliacao_estornos_falhados_total").increment(resultado.falhas());
            if (resultado.verificadas() > 0) {
                Log.infof("reconciliacao: %d baixas verificadas, %d com venda, %d estornadas, %d falhas",
                        resultado.verificadas(), resultado.confirmadas(), resultado.estornadas(), resultado.falhas());
            }
        } catch (Exception e) {
            // catalogo fora do ar, por exemplo: pula a rodada e tenta na proxima
            Log.warnf("reconciliacao pulada nesta rodada: %s", e.getMessage());
        }
    }
}
