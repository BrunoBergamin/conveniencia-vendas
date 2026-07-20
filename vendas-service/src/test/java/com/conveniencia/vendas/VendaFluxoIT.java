package com.conveniencia.vendas;

import com.conveniencia.vendas.application.venda.CatalogoPort;
import com.conveniencia.vendas.application.venda.ItemPrecificado;
import com.conveniencia.vendas.application.venda.ResultadoPrecificacao;
import com.conveniencia.vendas.domain.venda.Dinheiro;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Fluxo do vendas-service com o catalogo mockado (@InjectMock CatalogoPort).
 * Sobe o Quarkus e um Postgres (Dev Services), entao precisa de Docker (roda no CI).
 */
@QuarkusTest
class VendaFluxoIT {

    private static final String SEGREDO = "segredo-de-desenvolvimento-conveniencia-32b+";

    @InjectMock
    CatalogoPort catalogo;

    private String token(String login, String papel) {
        SecretKey key = Keys.hmacShaKeyFor(SEGREDO.getBytes(StandardCharsets.UTF_8));
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(login)
                .claim("papel", papel)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusSeconds(3600)))
                .signWith(key)
                .compact();
    }

    @Test
    void semTokenRetorna401() {
        given().when().get("/caixas/aberto").then().statusCode(401);
    }

    @Test
    void abreCaixaERegistraVenda() {
        String jwt = token("op-teste-1", "OPERADOR");

        // abre o caixa
        given().header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
                .body("{\"fundoTroco\": 100.00}")
                .when().post("/caixas")
                .then().statusCode(201);

        // catalogo devolve o item precificado (mock)
        UUID produto = UUID.randomUUID();
        when(catalogo.baixarEstoque(any(), any(), any())).thenReturn(new ResultadoPrecificacao(
                List.of(new ItemPrecificado(produto, "Coca 350ml", Dinheiro.de("5.50"), 2))));

        // registra a venda
        UUID chave = UUID.randomUUID();
        String corpo = "{\"chaveIdempotencia\":\"" + chave + "\",\"itens\":[{\"produtoId\":\""
                + produto + "\",\"quantidade\":2}],\"formaPagamento\":\"PIX\"}";
        String id = given().header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
                .body(corpo)
                .when().post("/vendas")
                .then().statusCode(201)
                .body("total", equalTo(11.00f))
                .extract().path("id");

        // IDEMPOTENCIA: repetir a MESMA requisicao (retry / clique duplo) NAO
        // cria outra venda — devolve a mesma, com o mesmo id.
        given().header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
                .body(corpo)
                .when().post("/vendas")
                .then().statusCode(201)
                .body("id", equalTo(id))
                .body("total", equalTo(11.00f));
    }

    @Test
    void vendaSemCaixaAbertoRetorna409() {
        String jwt = token("op-sem-caixa", "OPERADOR");
        UUID produto = UUID.randomUUID();

        given().header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
                .body("{\"chaveIdempotencia\":\"" + UUID.randomUUID() + "\",\"itens\":[{\"produtoId\":\""
                        + produto + "\",\"quantidade\":1}],\"formaPagamento\":\"DINHEIRO\"}")
                .when().post("/vendas")
                .then().statusCode(409)
                .body("erro", equalTo("CAIXA_NAO_ABERTO"));
    }
}
