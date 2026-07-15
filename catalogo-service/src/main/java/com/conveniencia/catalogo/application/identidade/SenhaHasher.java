package com.conveniencia.catalogo.application.identidade;

/** Porta de saida: hashing e conferencia de senha. */
public interface SenhaHasher {
    String hash(String senhaPura);
    boolean confere(String senhaPura, String hash);
}
