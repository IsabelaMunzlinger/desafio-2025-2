package br.com.desafiodev.locadora.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToMany
    private List<Exemplar> exemplares;

    private String nome;
    private String cpf;
    private String email;
    private String telefone;


    @Temporal(TemporalType.DATE)
    private Date dataLocacao;

    @Temporal(TemporalType.DATE)
    private Date dataDevolucao;

    @Temporal(TemporalType.DATE)
    private Date dataDevolvido;

}
