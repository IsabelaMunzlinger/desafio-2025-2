package br.com.desafiodev.locadora.service;

import br.com.desafiodev.locadora.dto.FilmeDTO;
import br.com.desafiodev.locadora.model.Filme;
import br.com.desafiodev.locadora.repository.FilmeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilmeService {

    @Autowired
    private ExemplarService exemplarService;

    @Autowired
    private FilmeRepository filmeRepository;

    private FilmeDTO dto;

    @Transactional
    public void salvarFilme(FilmeDTO dto) {
        // Verifica se já existe um filme com o mesmo título
        boolean filmeExistente = filmeRepository.verificaTituloIgnora(dto.titulo());
        if (filmeExistente) {
            throw new IllegalArgumentException("Já existe um filme com esse título.");
        }

        Filme filme = dto.id() != null ?
                filmeRepository.findById(dto.id())
                        .orElseThrow(() -> new IllegalArgumentException("Filme não encontrado")) :
                new Filme();

        // Configuração do filme
        filme.setTitulo(dto.titulo());
        filme.setResumo(dto.resumo());
        filme.setPontuacao(dto.pontuacao());
        filme.setLancamento(dto.lancamento().toString());
        filme.setAtivo(dto.ativo());
        filme.setExemplaresDisponiveis(0L); // Inicializa com 0 exemplares disponíveis

        // Salva o filme
        filme = filmeRepository.save(filme);

        // Adiciona exemplares (se houver)
        int quantidade = dto.exemplaresDisponiveis() != null ? dto.exemplaresDisponiveis().intValue() : 0;
        if (quantidade > 0) {
            exemplarService.adicionarExemplares(filme.getId(), quantidade, dto.ativo());
        }
    }
}
