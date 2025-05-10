package br.com.desafiodev.locadora.dto;

import java.time.LocalDate;

public record FilmeDTO (
    Long id,
    Long tmdbId,
    String titulo,
    String resumo,
    String pontuacao,
    LocalDate lancamento,
    boolean ativo,
    Long exemplaresDisponiveis){
}
