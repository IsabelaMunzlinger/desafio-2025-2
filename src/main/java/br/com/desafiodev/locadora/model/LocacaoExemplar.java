package br.com.desafiodev.locadora.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locacao_exemplar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Tabela pivot entre locação e exemplar
public class LocacaoExemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "locacao_id", nullable = false)
    private Locacao locacao;

    @ManyToOne
    @JoinColumn(name = "exemplar_id", nullable = false)
    private Exemplar exemplar;

}
