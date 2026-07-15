package com.conveniencia.catalogo.api.error;

import com.conveniencia.catalogo.application.identidade.CredenciaisInvalidasException;
import com.conveniencia.catalogo.application.produto.CodigoBarrasDuplicadoException;
import com.conveniencia.catalogo.domain.estoque.EstoqueInsuficienteException;
import com.conveniencia.catalogo.domain.shared.EntidadeNaoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** Traduz excecoes em respostas HTTP consistentes. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> naoEncontrado(EntidadeNaoEncontradaException ex) {
        return build(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", ex.getMessage());
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErrorResponse> semEstoque(EstoqueInsuficienteException ex) {
        return build(HttpStatus.CONFLICT, "SEM_ESTOQUE", ex.getMessage());
    }

    @ExceptionHandler(CodigoBarrasDuplicadoException.class)
    public ResponseEntity<ErrorResponse> duplicado(CodigoBarrasDuplicadoException ex) {
        return build(HttpStatus.CONFLICT, "CODIGO_BARRAS_DUPLICADO", ex.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErrorResponse> credenciais(CredenciaisInvalidasException ex) {
        return build(HttpStatus.UNAUTHORIZED, "CREDENCIAIS_INVALIDAS", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> ilegal(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacao(MethodArgumentNotValidException ex) {
        String detalhe = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "VALIDACAO", detalhe);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String erro, String mensagem) {
        return ResponseEntity.status(status).body(ErrorResponse.de(status.value(), erro, mensagem));
    }
}
