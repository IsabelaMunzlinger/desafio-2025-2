package br.com.desafiodev.locadora.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private boolean ativo;
    private Long exemplaresDisponiveis;

    // Ajustar para pegar dados da API
    private String titulo;
    private String resumo;
    private String pontuacao;


    @Temporal(TemporalType.DATE)
    private Date lancamento;
}
