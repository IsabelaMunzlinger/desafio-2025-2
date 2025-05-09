package br.com.desafiodev.locadora.dto;

import java.time.LocalDate;

public record FilmeDTO (
    Long tmdbId,
    String titulo,
    String resumo,
    String pontuacao,
    LocalDate lancamento,
    boolean ativo,
    int exemplaresDisponiveis){
}
