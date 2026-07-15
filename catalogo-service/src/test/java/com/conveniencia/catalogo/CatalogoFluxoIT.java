package com.conveniencia.catalogo;

import com.conveniencia.catalogo.api.auth.LoginRequest;
import com.conveniencia.catalogo.api.auth.LoginResponse;
import com.conveniencia.catalogo.api.estoque.BaixarEstoqueRequest;
import com.conveniencia.catalogo.api.estoque.BaixarEstoqueResponse;
import com.conveniencia.catalogo.api.estoque.EntradaEstoqueRequest;
import com.conveniencia.catalogo.api.estoque.EstoqueResponse;
import com.conveniencia.catalogo.api.produto.ProdutoRequest;
import com.conveniencia.catalogo.api.produto.ProdutoResponse;
import com.conveniencia.catalogo.domain.produto.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Fluxo ponta a ponta: login, cadastro, entrada, baixa e consulta de estoque. */
class CatalogoFluxoIT extends AbstractPostgresIT {

    @Autowired
    TestRestTemplate rest;

    private HttpHeaders autenticar(String login, String senha) {
        LoginResponse resp = rest.postForObject("/auth/login", new LoginRequest(login, senha), LoginResponse.class);
        assertNotNull(resp);
        assertNotNull(resp.token());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(resp.token());
        return headers;
    }

    @Test
    void fluxoCompletoDeVendaNoEstoque() {
        HttpHeaders gerente = autenticar("gerente", "gerente123");

        // cadastra produto
        var req = new ProdutoRequest("9990001", "Produto Teste", new BigDecimal("3.00"), Categoria.OUTROS);
        var criado = rest.exchange("/produtos", HttpMethod.POST, new HttpEntity<>(req, gerente), ProdutoResponse.class);
        assertEquals(201, criado.getStatusCode().value());
        UUID id = criado.getBody().id();

        // da entrada de 10
        var entrada = rest.exchange("/estoque/" + id + "/entrada", HttpMethod.POST,
                new HttpEntity<>(new EntradaEstoqueRequest(10), gerente), EstoqueResponse.class);
        assertEquals(10, entrada.getBody().quantidade());

        // baixa 4 pelo endpoint interno (o que o vendas-service usa)
        var baixaReq = new BaixarEstoqueRequest(List.of(new BaixarEstoqueRequest.Item(id, 4)));
        var baixa = rest.exchange("/internal/estoque/baixar", HttpMethod.POST,
                new HttpEntity<>(baixaReq, gerente), BaixarEstoqueResponse.class);
        assertEquals(200, baixa.getStatusCode().value());
        assertEquals(new BigDecimal("3.00"), baixa.getBody().itens().get(0).precoUnitario());

        // sobra 6
        var consulta = rest.exchange("/estoque/" + id, HttpMethod.GET,
                new HttpEntity<>(gerente), EstoqueResponse.class);
        assertEquals(6, consulta.getBody().quantidade());
    }

    @Test
    void operadorNaoPodeCadastrarProduto() {
        HttpHeaders operador = autenticar("operador", "operador123");
        var req = new ProdutoRequest("8880001", "Nao permitido", new BigDecimal("1.00"), Categoria.OUTROS);
        var resp = rest.exchange("/produtos", HttpMethod.POST, new HttpEntity<>(req, operador), String.class);
        assertEquals(403, resp.getStatusCode().value());
    }

    @Test
    void loginInvalidoRetorna401() {
        var resp = rest.postForEntity("/auth/login", new LoginRequest("gerente", "errada"), String.class);
        assertEquals(401, resp.getStatusCode().value());
    }
}
