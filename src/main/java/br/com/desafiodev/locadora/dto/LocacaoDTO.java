package br.com.desafiodev.locadora.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF; //Para validar o cpf

import java.time.LocalDate;
import java.util.List;

// Lombok: Gera automaticamente getters, setters e construtores
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocacaoDTO {

    // Identificador da locação
    private Long id;

    // Nome do cliente - não pode estar em branco
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    // CPF do cliente - obrigatório e validado com formato correto
    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    // Email do cliente - obrigatório e deve estar no formato válido
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    // Telefone do cliente - obrigatório e deve seguir o padrão (99) 99999-9999
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{5}-\\d{4}", message = "Telefone inválido. Use o formato (99) 99999-9999")
    private String telefone;

    // Data em que a locação foi feita - não pode ser nula
    @NotNull(message = "Data de locação é obrigatória")
    private LocalDate dataLocacao;

    // Data prevista para devolução - não pode ser nula
    @NotNull(message = "Data de devolução é obrigatória")
    private LocalDate dataDevolucao;

    // Lista de IDs dos exemplares alugados - deve ter pelo menos 1 e no máximo 3 itens
    @NotEmpty(message = "Selecione ao menos 1 exemplar")
    @Size(max = 3, message = "Você pode selecionar no máximo 3 exemplares")
    private List<Long> exemplarIds;
}
