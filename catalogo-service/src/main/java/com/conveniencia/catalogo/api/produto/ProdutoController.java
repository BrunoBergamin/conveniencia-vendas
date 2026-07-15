package com.conveniencia.catalogo.api.produto;

import com.conveniencia.catalogo.application.produto.AtualizacaoProduto;
import com.conveniencia.catalogo.application.produto.NovoProduto;
import com.conveniencia.catalogo.application.produto.ProdutoApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos")
public class ProdutoController {

    private final ProdutoApplicationService produtos;

    public ProdutoController(ProdutoApplicationService produtos) {
        this.produtos = produtos;
    }

    @GetMapping
    @Operation(summary = "Lista os produtos ativos")
    public List<ProdutoResponse> listar() {
        return produtos.listar().stream().map(ProdutoResponse::de).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto por id")
    public ProdutoResponse buscar(@PathVariable UUID id) {
        return ProdutoResponse.de(produtos.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Cadastra um produto (GERENTE)")
    public ProdutoResponse cadastrar(@Valid @RequestBody ProdutoRequest req) {
        var produto = produtos.cadastrar(
                new NovoProduto(req.codigoBarras(), req.descricao(), req.preco(), req.categoria()));
        return ProdutoResponse.de(produto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Atualiza um produto (GERENTE)")
    public ProdutoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody AtualizarProdutoRequest req) {
        var produto = produtos.atualizar(id,
                new AtualizacaoProduto(req.descricao(), req.preco(), req.categoria()));
        return ProdutoResponse.de(produto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Inativa um produto (GERENTE)")
    public void inativar(@PathVariable UUID id) {
        produtos.inativar(id);
    }
}
