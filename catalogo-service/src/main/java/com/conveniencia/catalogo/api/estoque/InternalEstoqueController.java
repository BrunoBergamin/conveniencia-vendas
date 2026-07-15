package com.conveniencia.catalogo.api.estoque;

import com.conveniencia.catalogo.application.estoque.EstoqueApplicationService;
import com.conveniencia.catalogo.application.estoque.ItemBaixa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint interno consumido pelo vendas-service ao registrar uma venda.
 * Exige apenas usuario autenticado (o vendas repassa o JWT do operador).
 */
@RestController
@RequestMapping("/internal/estoque")
@Tag(name = "Estoque (interno)")
public class InternalEstoqueController {

    private final EstoqueApplicationService estoque;

    public InternalEstoqueController(EstoqueApplicationService estoque) {
        this.estoque = estoque;
    }

    @PostMapping("/baixar")
    @Operation(summary = "Baixa o estoque de varios itens (usado na venda)")
    public BaixarEstoqueResponse baixar(@Valid @RequestBody BaixarEstoqueRequest req) {
        List<ItemBaixa> itens = req.itens().stream()
                .map(i -> new ItemBaixa(i.produtoId(), i.quantidade()))
                .toList();
        return BaixarEstoqueResponse.de(estoque.baixar(itens));
    }
}
