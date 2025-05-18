package br.com.desafiodev.locadora.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exemplares")
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cada exemplar está associado a um único filme
    @ManyToOne
    @JoinColumn(name = "filme_id", nullable = true)
    private Filme filme;

    // Data em que o exemplar foi cadastrado
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;

    // Indica se o exemplar está ativo
    private boolean ativo;

    // Lista de locações que envolveram este exemplar
    @OneToMany(mappedBy = "exemplar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocacaoExemplar> locacoesExemplares;

}