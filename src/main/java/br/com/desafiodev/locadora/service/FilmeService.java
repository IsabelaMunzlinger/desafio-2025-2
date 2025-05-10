package br.com.desafiodev.locadora.service;

import br.com.desafiodev.locadora.dto.FilmeDTO;
import br.com.desafiodev.locadora.model.Exemplar;
import br.com.desafiodev.locadora.model.Filme;
import br.com.desafiodev.locadora.repository.FilmeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class FilmeService {

    @Autowired
    private FilmeRepository filmeRepository;

    @Transactional
    public void salvarFilme(FilmeDTO dto) {
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
        filme.setExemplaresDisponiveis(1L); // Inicializa com 1

        // Salva o filme primeiro
        filme = filmeRepository.save(filme);

        // Adiciona exemplares diretamente (sem chamar outro serviço)
        int quantidade = dto.exemplaresDisponiveis() != null ? dto.exemplaresDisponiveis().intValue() : 0;
        if (quantidade > 0) {
            adicionarExemplares(filme, quantidade, dto.ativo());
        }
    }

    private void adicionarExemplares(Filme filme, int quantidade, Boolean ativo) {
        if (quantidade <= 0) return;

        for (int i = 0; i < quantidade; i++) {
            Exemplar exemplar = new Exemplar();
            exemplar.setAtivo(ativo != null ? ativo : true);
            exemplar.setDataCadastro(new Date());

            exemplar.setFilme(filme);
            filme.getExemplares().add(exemplar);
        }

        filme.setExemplaresDisponiveis(filme.getExemplaresDisponiveis() + quantidade);

        // Salvar o filme salva automaticamente os exemplares
        filmeRepository.save(filme);
    }
}