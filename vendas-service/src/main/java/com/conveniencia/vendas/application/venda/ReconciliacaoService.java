package com.conveniencia.vendas.application.venda;

import com.conveniencia.vendas.domain.venda.VendaRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Rede de seguranca da saga da venda: encontra baixas de estoque que ficaram
 * ORFAS (efetivadas no catalogo, mas sem venda correspondente aqui) e pede o
 * estorno. Cobre os dois buracos que a compensacao inline nao alcanca: o
 * processo morrer entre a baixa e o salvar da venda, e o proprio estorno da
 * compensacao falhar (o log RECONCILIAR).
 *
 * Rodar varias vezes e seguro: a baixa e o estorno sao idempotentes por chave,
 * e o limite de idade garante que vendas em andamento nunca entram na conta.
 */
@ApplicationScoped
public class ReconciliacaoService {

    @Inject
    VendaRepository vendas;
    @Inject
    CatalogoPort catalogo;

    public Resultado reconciliar(Instant limite, String autorizacao) {
        List<UUID> chaves = catalogo.baixasEfetivadasAntesDe(limite, autorizacao);
        int confirmadas = 0;
        int estornadas = 0;
        int falhas = 0;
        for (UUID chave : chaves) {
            if (vendas.buscarPorChave(chave).isPresent()) {
                confirmadas++;
                continue;
            }
            try {
                catalogo.estornarBaixa(chave, autorizacao);
                estornadas++;
                Log.warnf("reconciliacao: baixa %s estava sem venda; estoque estornado", chave);
            } catch (Exception e) {
                falhas++;
                Log.errorf(e, "RECONCILIAR: baixa %s sem venda e estorno falhou; proxima rodada tenta de novo", chave);
            }
        }
        return new Resultado(chaves.size(), confirmadas, estornadas, falhas);
    }

    public record Resultado(int verificadas, int confirmadas, int estornadas, int falhas) {
    }
}
