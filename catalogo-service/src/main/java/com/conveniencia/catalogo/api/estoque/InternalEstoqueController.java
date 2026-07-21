package com.conveniencia.catalogo.api.estoque;

import com.conveniencia.catalogo.application.estoque.EstoqueApplicationService;
import com.conveniencia.catalogo.application.estoque.ItemBaixa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Endpoints internos consumidos pelo vendas-service ao registrar uma venda.
 * Exige apenas usuario autenticado (o vendas repassa o JWT do operador).
 *
 * O header Idempotency-Key (chave unica da venda, gerada pelo vendas-service)
 * torna a baixa segura para retry e permite o estorno exato na compensacao.
 */
@RestController
@RequestMapping("/internal/estoque")
@Tag(name = "Estoque (interno)")
public class InternalEstoqueController {

    static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private final EstoqueApplicationService estoque;

    public InternalEstoqueController(EstoqueApplicationService estoque) {
        this.estoque = estoque;
    }

    @PostMapping("/baixar")
    @Operation(summary = "Baixa o estoque de varios itens (idempotente pela Idempotency-Key)")
    public BaixarEstoqueResponse baixar(@RequestHeader(IDEMPOTENCY_KEY) UUID chave,
                                        @Valid @RequestBody BaixarEstoqueRequest req) {
        List<ItemBaixa> itens = req.itens().stream()
                .map(i -> new ItemBaixa(i.produtoId(), i.quantidade()))
                .toList();
        return BaixarEstoqueResponse.de(estoque.baixar(chave, itens));
    }

    @PostMapping("/estornar")
    @Operation(summary = "Estorna uma baixa (compensacao de venda que falhou); idempotente")
    public EstornoResponse estornar(@RequestHeader(IDEMPOTENCY_KEY) UUID chave) {
        return new EstornoResponse(estoque.estornar(chave));
    }

    @GetMapping("/operacoes-efetivadas")
    @Operation(summary = "Chaves de baixas efetivadas antes do instante (job de reconciliacao)")
    public OperacoesEfetivadasResponse operacoesEfetivadas(@RequestParam("antesDe") Instant antesDe) {
        return new OperacoesEfetivadasResponse(estoque.baixasEfetivadasAntesDe(antesDe));
    }

    /** estornada=false significa no-op: chave desconhecida ou ja estornada antes. */
    public record EstornoResponse(boolean estornada) {
    }

    public record OperacoesEfetivadasResponse(List<UUID> chaves) {
    }
}
