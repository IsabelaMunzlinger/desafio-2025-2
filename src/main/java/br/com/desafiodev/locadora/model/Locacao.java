package br.com.desafiodev.locadora.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private List<Long> exemplarIds;

    private String nome;
    private String cpf;
    private String email;
    private String telefone;

    @NotNull
    private LocalDate dataLocacao;

    @NotNull
    private LocalDate dataDevolucao;

    @Temporal(TemporalType.DATE)
    private Date dataDevolvido;

    @Lob
    @Column(columnDefinition = "TEXT") //TEXT para ter espaço para armazenar o valor
    private String qrCode;

    @OneToMany(mappedBy = "locacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocacaoExemplar> locacoesExemplares;

    // Para formatar as datas de locação e devolução
    public String getDataLocacaoFormatada() {
        if (dataLocacao == null) return "";
        return dataLocacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataDevolucaoFormatada() {
        if (dataDevolucao == null) return "";
        return dataDevolucao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

}
