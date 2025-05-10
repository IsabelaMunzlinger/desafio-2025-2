package br.com.desafiodev.locadora.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.antlr.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Table(name = "filmes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean ativo;

    @NotNull
    @Max(10)
    private Long exemplaresDisponiveis = 1L;


    @OneToMany(mappedBy = "filme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exemplar> exemplares = new ArrayList<>();


    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String resumo;

    @NotBlank(message = "A pontuação é obrigatória")
    private String pontuacao;

    // Como String, pois vem da API como string
    @NotBlank(message = "A data de lançamento é obrigatória")
    private String lancamento;

//    public void adicionarExemplar(Exemplar exemplar) {
//        exemplar.setFilme(this);
//        exemplares.add(exemplar);
//    }

    // Para poder converter para LocalDate
    public LocalDate getLancamentoLocalDate() {
        return lancamento != null && !lancamento.isEmpty() ? LocalDate.parse(lancamento) : null;
    }
}