package br.com.desafiodev.locadora.service;

import br.com.desafiodev.locadora.dto.FilmeDTO;
import br.com.desafiodev.locadora.model.Filme;
import br.com.desafiodev.locadora.repository.FilmeRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // Indica que essa classe é um componente de serviço do Spring
public class FilmeService {

    @Autowired
    private ExemplarService exemplarService; // Serviço para manipular exemplares de filmes

    @Autowired
    private FilmeRepository filmeRepository; // Repositório para acesso ao banco de dados de filmes

    private FilmeDTO dto;

    @Transactional // Para todas as operações dentro do método sejam executadas de uma vez
    public void salvarFilme(FilmeDTO dto) {
        // Verifica se já existe um filme com o mesmo título (ignora letras maiúsculas/minúsculas)
        boolean filmeExistente = filmeRepository.verificaTituloIgnora(dto.titulo());
        if (filmeExistente) {
            // Se existir, lança exceção informando o conflito
            throw new IllegalArgumentException("Já existe um filme com esse título.");
        }

        // Se o DTO tiver um ID, tenta buscar o filme para atualização; senão, cria um novo
        Filme filme = dto.id() != null ?
                filmeRepository.findById(dto.id())
                        .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado")) :
                new Filme();

        // Define os atributos do filme com base nos dados recebidos do DTO
        filme.setTitulo(dto.titulo());
        filme.setResumo(dto.resumo());
        filme.setPontuacao(dto.pontuacao());
        filme.setLancamento(dto.lancamento().toString());
        filme.setAtivo(dto.ativo());
        filme.setExemplaresDisponiveis(0L); // Inicializa com 0 exemplares disponíveis

        // Salva ou atualiza o filme no banco de dados
        filme = filmeRepository.save(filme);

        // Se o DTO informar uma quantidade de exemplares, adiciona ao filme recém-salvo
        int quantidade = dto.exemplaresDisponiveis() != null ? dto.exemplaresDisponiveis().intValue() : 0;
        if (quantidade > 0) {
            exemplarService.adicionarExemplares(filme.getId(), quantidade, dto.ativo());
        }
    }

    // Retorna todos os filmes ativos que têm pelo menos 1 exemplar disponível
    public List<Filme> buscarFilmesComExemplaresDisponiveis() {
        return filmeRepository.findByAtivoTrueAndExemplaresDisponiveisGreaterThan(0);
    }
}
