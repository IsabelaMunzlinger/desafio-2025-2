package br.com.desafiodev.locadora.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    private Filme filme;


    @Temporal(TemporalType.DATE)
    private Date dataCadastro;

    private boolean ativo;
}
