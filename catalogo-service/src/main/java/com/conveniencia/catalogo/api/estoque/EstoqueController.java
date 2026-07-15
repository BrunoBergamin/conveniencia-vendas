package com.conveniencia.catalogo.api.estoque;

import com.conveniencia.catalogo.application.estoque.EstoqueApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/estoque")
@Tag(name = "Estoque")
public class EstoqueController {

    private final EstoqueApplicationService estoque;

    public EstoqueController(EstoqueApplicationService estoque) {
        this.estoque = estoque;
    }

    @GetMapping("/{produtoId}")
    @Operation(summary = "Consulta o estoque de um produto")
    public EstoqueResponse consultar(@PathVariable UUID produtoId) {
        return EstoqueResponse.de(estoque.consultar(produtoId));
    }

    @PostMapping("/{produtoId}/entrada")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Da entrada de estoque em um produto (GERENTE)")
    public EstoqueResponse darEntrada(@PathVariable UUID produtoId, @Valid @RequestBody EntradaEstoqueRequest req) {
        return EstoqueResponse.de(estoque.darEntrada(produtoId, req.quantidade()));
    }
}
